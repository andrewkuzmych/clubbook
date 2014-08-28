package com.nl.clubbook.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.NavDrawerItem;
import com.nl.clubbook.datasource.ChatMessageDto;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.JSONConverter;
import com.nl.clubbook.fragment.ChatFragment;
import com.nl.clubbook.fragment.ClubFragment;
import com.nl.clubbook.fragment.ShareFragment;
import com.nl.clubbook.fragment.dialog.ProgressDialog;
import com.nl.clubbook.helper.CheckInOutCallbackInterface;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.ui.drawer.NavDrawerData;
import com.nl.clubbook.ui.drawer.NavDrawerListAdapter;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.fragment.BaseFragment;
import com.nl.clubbook.fragment.ClubsListFragment;
import com.nl.clubbook.fragment.FriendsFragment;
import com.nl.clubbook.fragment.MessagesFragment;
import com.nl.clubbook.fragment.SettingsFragment;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.NotificationHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.L;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 5/19/14
 * Time: 1:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        BaseFragment.OnInnerFragmentDestroyedListener, BaseFragment.OnInnerFragmentOpenedListener,
        ClubFragment.OnCheckInCheckOutListener {

    public static final String ACTION_CHECK_IN = "ACTION_CHECK_IN";
    public static final String ACTION_CHECK_OUT = "ACTION_CHECK_OUT";

    public static final String EXTRA_TYPE = "EXTRA_TYPE";

    public static final String TYPE_CHAT = "chat";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private View mNavDrawerHeaderView;
    private ActionBarDrawerToggle mDrawerToggle;

    private TextView actionbarChatCount;
    private Integer chatCountOfNewMessages = 0;

    private HashMap<Integer, BaseFragment> fragmentMap = new HashMap<Integer, BaseFragment>();
    private List<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter mAdapter;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    private Fragment mCurrentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);

        if (!getSession().isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), MainLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        fragmentMap.put(NavDrawerData.CLUB_LIST_POSITION, new ClubsListFragment());
        fragmentMap.put(NavDrawerData.MESSAGES_POSITION, new MessagesFragment());
        fragmentMap.put(NavDrawerData.FRIENDS_POSITION, new FriendsFragment());
        fragmentMap.put(NavDrawerData.SHARE_POSITION, new ShareFragment());
        fragmentMap.put(NavDrawerData.SETTINGS_POSITION, new SettingsFragment());

        initReceivers();

        initImageLoader();
        initActionBar();
        initNavDrawer();

        loadData();
    }

    @Override
    public void onStart() {
        super.onStart();
        SessionManager session = SessionManager.getInstance();
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get(SessionManager.KEY_ID);
        subscribeToChannel("message_" + userId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        L.d("on New intent - display default view");

        displayDefaultView();
    }

    @Override
    public void onStop() {
        super.onStop();
        SessionManager session = SessionManager.getInstance();
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get(SessionManager.KEY_ID);
        NotificationHelper.pubnub.unsubscribe("message_" + userId);
    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(mCheckInReceiver);
        unregisterReceiver(mCheckOutReceiver);

        LocationCheckinHelper.getInstance().cancelLocationUpdates(MainActivity.this);

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == EditProfileActivity.REQUEST_CODE) {
            fillNavDrawerHeader();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        View badgeMessages = MenuItemCompat.getActionView(menu.findItem(R.id.badgeMessages));
        RelativeLayout messagesActionBar = (RelativeLayout) badgeMessages.findViewById(R.id.messagesActionBar);
        ImageButton actionbarChatButton = (ImageButton) messagesActionBar.findViewById(R.id.actionbarChatButton);
        actionbarChatCount = (TextView) messagesActionBar.findViewById(R.id.actionbarChatCount);
        actionbarChatCount.setText(String.valueOf(chatCountOfNewMessages));

        actionbarChatButton.setOnClickListener(MainActivity.this);

        // update count of messages
        updateMessagesCount();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fManager = getSupportFragmentManager();
        if(fManager.getBackStackEntryCount() > 0) {
            fManager.popBackStack();
        } else {
            finish();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.actionbarChatButton:
                onChatBtnClicked();
                break;
            case R.id.holderNavDrawerHeader:
                onNavDrawerHeaderClicked();
                break;
            case R.id.imgCheckOut:
                onImgCheckOutClicked();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //list header has positions equals '0'
        displayView(position - 1);
    }

    @Override
    protected void loadData() {
        displayDefaultView();
    }

    @Override
    public void onInnerFragmentDestroyed() {
        if (!mDrawerToggle.isDrawerIndicatorEnabled()) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void onInnerFragmentOpened() {
        if (mDrawerToggle.isDrawerIndicatorEnabled()) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void onCheckedIn() {
        updateNavDrawerHeader();
    }

    @Override
    public void onCheckedOut() {
        updateNavDrawerHeader();
    }

    private void initImageLoader() {
        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_avatar_missing)
                .showImageForEmptyUri(R.drawable.ic_avatar_missing)
                .showImageOnFail(R.drawable.ic_avatar_unknown)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
    }

    private void initNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.listDrawer);
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout,
                R.drawable.ic_drawer,  R.string.app_name, R.string.app_name) { //TODO
            public void onDrawerClosed(View view) {
//                getSupportActionBar().setTitle("Settings");
//                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
//                getSupportActionBar().setTitle("Settings1");
//                invalidateOptionsMenu();
            }
        };
        navDrawerItems = NavDrawerData.getNavDrawerItems(MainActivity.this);

//        // set adapter
        View navDrawerHeaderView = initNavDrawerHeader();
        mAdapter = new NavDrawerListAdapter(MainActivity.this, navDrawerItems);
        mDrawerList.addHeaderView(navDrawerHeaderView);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(MainActivity.this);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initReceivers() {
        registerReceiver(mCheckInReceiver, new IntentFilter(ACTION_CHECK_IN));
        registerReceiver(mCheckOutReceiver, new IntentFilter(ACTION_CHECK_OUT));
    }

    private View initNavDrawerHeader() {
        mNavDrawerHeaderView = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_nav_drawer_header, null);
        mNavDrawerHeaderView.findViewById(R.id.holderNavDrawerHeader).setOnClickListener(MainActivity.this);

        fillNavDrawerHeader();

        return mNavDrawerHeaderView;
    }

    private void fillNavDrawerHeader() {
        HashMap<String, String> user = getSession().getUserDetails();

        ImageView imgAvatar = (ImageView) mNavDrawerHeaderView.findViewById(R.id.imgAvatar);
        TextView txtProfileName = (TextView) mNavDrawerHeaderView.findViewById(R.id.txtProfileName);
        TextView txtProfileInfo = (TextView) mNavDrawerHeaderView.findViewById(R.id.txtProfileInfo);

        String userAvatarUrl = user.get(SessionManager.KEY_AVATAR);
        if (userAvatarUrl != null) {
            userAvatarUrl = ImageHelper.getUserAvatar(userAvatarUrl);
            mImageLoader.displayImage(userAvatarUrl, imgAvatar, mOptions);
        }

        String profileName = user.get(SessionManager.KEY_NAME);
        String profileAge = user.get(SessionManager.KEY_AGE);
        String profileGender = user.get(SessionManager.KEY_GENDER);

        txtProfileName.setText(profileName != null ? profileName : "");
        txtProfileInfo.setText((profileAge != null && profileAge.length() > 0) ? profileAge + ", " : "");
        txtProfileInfo.append(profileGender != null ? profileGender : "");

        mNavDrawerHeaderView.findViewById(R.id.holderUserInfo).setOnClickListener(MainActivity.this);

        updateNavDrawerHeader();
    }

    private void handleNotification(JSONObject messageJson) {
        if(mCurrentFragment instanceof MessagesFragment) {

            MessagesFragment messagesFragment = (MessagesFragment) mCurrentFragment;
            ChatFragment chatFragment = messagesFragment.getChatFragment();

            if (chatFragment != null && TYPE_CHAT.equalsIgnoreCase(messageJson.optString("type"))) {
                JSONObject data = messageJson.optJSONObject("data");
                String userTo = data.optString("user_to");
                String userFrom = data.optString("user_from");

                SessionManager session = SessionManager.getInstance();
                if (session.getConversationListner() != null && session.getConversationListner().equalsIgnoreCase(userFrom + "_" + userTo)) {
                    ChatMessageDto lastMessage = JSONConverter.newChatMessage(data.optJSONObject("last_message"));
                    chatFragment.receiveComment(lastMessage);
                } else {
                    updateMessagesCount();
                }
            } else {
                updateMessagesCount();
            }
        } else {
            updateMessagesCount();
        }
    }

    private void displayDefaultView() {
        Intent intent = getIntent();

        int displayView = NavDrawerData.DEFAULT_FRAGMENT_NUMBER;
        if (intent.hasExtra(EXTRA_TYPE)) {
            if (TYPE_CHAT.equalsIgnoreCase(intent.getStringExtra(EXTRA_TYPE))) {
                displayView = NavDrawerData.MESSAGES_POSITION;
            }
        }

        displayView(displayView);
    }

    public void updateMessagesCount() {
        // if user on List of All messages fragment then reload data
        if (mCurrentFragment instanceof MessagesFragment) {
            ((MessagesFragment) mCurrentFragment).onRefresh();
        }

        // retrieve the count of not read messages and update UI
        DataStore.getNotifications(getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (!failed) {
                    setMessageCount(Integer.parseInt((String) result));
                }
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

        initActionBar("");
    }

    private void updateNavDrawerHeader() {
        ClubDto club = LocationCheckinHelper.getInstance().getCurrentClub();

        TextView txtClubName = (TextView) mNavDrawerHeaderView.findViewById(R.id.txtClubName);
        View imgCheckOut = mNavDrawerHeaderView.findViewById(R.id.imgCheckOut);

        if(club != null && club.getId() != null && club.getId().length() > 0) {
            txtClubName.setText(club.getTitle());
            imgCheckOut.setTag(club.getId());
            imgCheckOut.setOnClickListener(MainActivity.this);

            mNavDrawerHeaderView.findViewById(R.id.holderCheckOut).setVisibility(View.VISIBLE);

            LocationCheckinHelper.getInstance().startLocationUpdate(MainActivity.this);
        } else {
            imgCheckOut.setTag(null);

            mNavDrawerHeaderView.findViewById(R.id.holderCheckOut).setVisibility(View.GONE);
        }
    }

    private void displayView(final int position) {
        mCurrentFragment = fragmentMap.get(position);

        if (mCurrentFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();

            mFragmentTransaction.replace(R.id.frame_container, mCurrentFragment);
            mFragmentTransaction.commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            L.i("Error in creating fragment");
        }
    }

    private void subscribeToChannel(String channel_name) {
        try {
            NotificationHelper.pubnub.subscribe(channel_name, callback);
        } catch (PubnubException e) {
            L.v("" + e);
        }
    }

    private void setMessageCount(Integer count) {
        chatCountOfNewMessages = count;

        navDrawerItems.get(NavDrawerData.MESSAGES_POSITION).setCount(chatCountOfNewMessages);
        mAdapter.notifyDataSetChanged();

        actionbarChatCount.setText(String.valueOf(chatCountOfNewMessages));
    }

    private void onChatBtnClicked() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();

        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.replace(R.id.frame_container, new MessagesFragment()).commit();
    }

    private void onNavDrawerHeaderClicked() {
        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivityForResult(intent, EditProfileActivity.REQUEST_CODE);
    }

    private void onImgCheckOutClicked() {
        showProgressDialog(getString(R.string.checking_out));

        LocationCheckinHelper.getInstance().checkOut(MainActivity.this, new CheckInOutCallbackInterface() {
            @Override
            public void onCheckInOutFinished(boolean isUserCheckOut) {
                handleCheckInCheckOutResults(isUserCheckOut);
            }
        });
    }

    private void hideProgressDialog() {
        DialogFragment dialogFragment = (DialogFragment)getSupportFragmentManager().findFragmentByTag(ProgressDialog.TAG);
        if(dialogFragment != null) {
            dialogFragment.dismissAllowingStateLoss();
        }
    }

    private void showProgressDialog(String message) {
        Fragment dialogFragment = ProgressDialog.newInstance(getString(R.string.app_name), message);
        getSupportFragmentManager().beginTransaction().add(dialogFragment, ProgressDialog.TAG).commitAllowingStateLoss();
    }

    private void handleCheckInCheckOutResults(boolean result) {
        if(isFinishing()) {
            return;
        }

        if (result) {
            mNavDrawerHeaderView.findViewById(R.id.holderCheckOut).setVisibility(View.GONE);
            hideProgressDialog();
        } else {
            Toast.makeText(MainActivity.this, R.string.problem_occurred_please_try_again, Toast.LENGTH_SHORT).show();
        }
    }

    private Callback callback = new Callback() {
        @Override
        public void connectCallback(String channel, Object message) {
            System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                    + " : " + message.getClass() + " : "
                    + message.toString());
        }

        @Override
        public void disconnectCallback(String channel, Object message) {
            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                    + " : " + message.getClass() + " : "
                    + message.toString());
        }

        public void reconnectCallback(String channel, Object message) {
            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                    + " : " + message.getClass() + " : "
                    + message.toString());
        }

        @Override
        public void successCallback(String channel, final Object message) {
            System.out.println("SUBSCRIBE : " + channel + " : "
                    + message.getClass() + " : " + message.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject messageJson = (JSONObject) message;
                    handleNotification(messageJson);

                }
            });
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                    + " : " + error.toString());
        }
    };

    private BroadcastReceiver mCheckInReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String clubName = getSession().getValueByKey(SessionManager.KEY_CHECKIN_CLUB_NAME);
            TextView txtClubName = (TextView) mNavDrawerHeaderView.findViewById(R.id.txtClubName);
            txtClubName.setText(clubName);

            mNavDrawerHeaderView.findViewById(R.id.holderCheckOut).setVisibility(View.VISIBLE);
        }
    };

    private BroadcastReceiver mCheckOutReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            TextView txtClubName = (TextView) mNavDrawerHeaderView.findViewById(R.id.txtClubName);
            txtClubName.setText("");

            mNavDrawerHeaderView.findViewById(R.id.holderCheckOut).setVisibility(View.GONE);
        }
    };
}

