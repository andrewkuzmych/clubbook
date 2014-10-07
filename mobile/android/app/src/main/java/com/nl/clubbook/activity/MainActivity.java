package com.nl.clubbook.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
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
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.JSONConverter;
import com.nl.clubbook.fragment.BaseFragment;
import com.nl.clubbook.fragment.ChatFragment;
import com.nl.clubbook.fragment.ClubFragment;
import com.nl.clubbook.fragment.ClubsListFragment;
import com.nl.clubbook.fragment.FriendsFragment;
import com.nl.clubbook.fragment.MessagesFragment;
import com.nl.clubbook.fragment.SettingsFragment;
import com.nl.clubbook.fragment.dialog.MessageDialog;
import com.nl.clubbook.helper.CheckInOutCallbackInterface;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.helper.NotificationReceiver;
import com.nl.clubbook.helper.NotificationHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.ui.drawer.NavDrawerData;
import com.nl.clubbook.ui.drawer.NavDrawerListAdapter;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.squareup.picasso.Picasso;

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
        MessageDialog.MessageDialogListener, SettingsFragment.OnLogOutListener {

    public static final String ACTION_CHECK_IN_CHECK_OUT = "ACTION_CHECK_IN_CHECK_OUT";

    public static final int MENU_ITEM_ADD_FRIEND = 0;
    public static final int MENU_ITEM_MESSAGES_INDEX = 1;

    private MenuItem mMenuMessages;
    private MenuItem mMenuAddFriend;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private View mNavDrawerHeaderView;
    private ActionBarDrawerToggle mDrawerToggle;

    private TextView actionbarChatCount;
    private Integer chatCountOfNewMessages = 0;

    private HashMap<Integer, BaseFragment> fragmentMap = new HashMap<Integer, BaseFragment>();
    private List<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter mAdapter;

    private Fragment mCurrentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);


        initReceivers();

        if (!getSession().isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), MainLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        fragmentMap.put(NavDrawerData.CLUB_LIST_POSITION, new ClubsListFragment());
        fragmentMap.put(NavDrawerData.MESSAGES_POSITION, new MessagesFragment());
        fragmentMap.put(NavDrawerData.FRIENDS_POSITION, new FriendsFragment());

        initActionBar();
        initNavDrawer();

        loadData();
        loadConfig();
    }

    @Override
    public void onStart() {
        super.onStart();

        subscribePubnup();
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

        unsubscribePubnup();
    }

    @Override
    protected void onDestroy() {
        fragmentMap.clear();

        unregisterReceiver(mCheckInCheckOutReceiver);

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
        inflater.inflate(R.menu.menu_add_friends, menu);
        inflater.inflate(R.menu.main_activity_actions, menu);

        View badgeMessages = MenuItemCompat.getActionView(menu.findItem(R.id.badgeMessages));
        RelativeLayout messagesActionBar = (RelativeLayout) badgeMessages.findViewById(R.id.messagesActionBar);
        ImageButton actionbarChatButton = (ImageButton) messagesActionBar.findViewById(R.id.actionbarChatButton);
        actionbarChatCount = (TextView) messagesActionBar.findViewById(R.id.actionbarChatCount);
        actionbarChatCount.setText(String.valueOf(chatCountOfNewMessages));

        actionbarChatButton.setOnClickListener(MainActivity.this);

        // update count of messages
        updateMessagesCount();

        mMenuMessages = menu.getItem(MENU_ITEM_MESSAGES_INDEX);
        mMenuAddFriend = menu.getItem(MENU_ITEM_ADD_FRIEND);
        mMenuAddFriend.setVisible(false);

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
            case R.id.menuAdd:
                onMenuAddClicked();
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
            case R.id.holderUserInfo:
                onNavDrawerHeaderClicked();
                break;
            case R.id.imgCheckOut:
                onImgCheckOutClicked();
                break;
            case R.id.txtClubName:
                onClubClicked((String)v.getTag());
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position - 1 == NavDrawerData.SHARE_POSITION) {
            mDrawerLayout.closeDrawer(mDrawerList);
            onShareClicked();
            return;
        }

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
    public void onPositiveButtonClick(MessageDialog dialogFragment) {
        doCheckOut();
    }

    @Override
    public void onNegativeButtonClick(MessageDialog dialogFragment) {
        dialogFragment.dismissAllowingStateLoss();
    }

    @Override
    public void onLogOut() {
        unsubscribePubnup();
        getSession().logoutUser();

        LocationCheckinHelper.clear();

        Intent intent = new Intent(MainActivity.this, MainLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void initNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.listDrawer);
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout,
                R.drawable.ic_drawer,  R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
            }

            public void onDrawerOpened(View drawerView) {
                sendScreenStatistic(R.string.sidebar_screen_android);
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
        registerReceiver(mCheckInCheckOutReceiver, new IntentFilter(ACTION_CHECK_IN_CHECK_OUT));
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
            Picasso.with(getBaseContext()).load(userAvatarUrl).error(R.drawable.ic_avatar_unknown).into(imgAvatar);
        }

        String profileName = user.get(SessionManager.KEY_NAME);
        String profileAge = user.get(SessionManager.KEY_AGE);
        String profileGender = user.get(SessionManager.KEY_GENDER);

        txtProfileName.setText(profileName != null ? profileName : "");
        txtProfileInfo.setText((profileAge != null && profileAge.length() > 0) ? profileAge + ", " : "");
        txtProfileInfo.append(profileGender != null ? profileGender : "");

        mNavDrawerHeaderView.findViewById(R.id.holderUserInfo).setOnClickListener(MainActivity.this);
        mNavDrawerHeaderView.findViewById(R.id.txtClubName).setOnClickListener(MainActivity.this);

        updateNavDrawerHeader();
    }

    private void handleNotification(JSONObject messageJson) {
        if(mCurrentFragment instanceof MessagesFragment) {

            MessagesFragment messagesFragment = (MessagesFragment) mCurrentFragment;
            ChatFragment chatFragment = messagesFragment.getChatFragment();

            if (chatFragment != null && NotificationReceiver.TYPE_CHAT.equalsIgnoreCase(messageJson.optString("type"))) {
                JSONObject data = messageJson.optJSONObject("data");
                String userTo = data.optString("user_to");
                String userFrom = data.optString("user_from");

                SessionManager session = SessionManager.getInstance();
                if (session.getConversationListener() != null && session.getConversationListener().equalsIgnoreCase(userFrom + "_" + userTo)) {
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

    private void unsubscribePubnup() {
        SessionManager session = SessionManager.getInstance();
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get(SessionManager.KEY_ID);
        NotificationHelper.pubnub.unsubscribe("message_" + userId);
    }

    private void subscribePubnup() {
        SessionManager session = SessionManager.getInstance();
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get(SessionManager.KEY_ID);
        subscribeToChannel("message_" + userId);
    }

    private void displayDefaultView() {
        Intent intent = getIntent();

        int displayView = NavDrawerData.DEFAULT_FRAGMENT_NUMBER;
        if (intent.hasExtra(NotificationReceiver.EXTRA_TYPE)) {
            String type = intent.getStringExtra(NotificationReceiver.EXTRA_TYPE);

            if(NotificationReceiver.TYPE_FRIENDS.equalsIgnoreCase(type)) {
                displayView = NavDrawerData.FRIENDS_POSITION;
            } else {
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

    private void loadConfig() {
        if(!NetworkUtils.isOn(MainActivity.this)) {
            return;
        }

        DataStore.getConfig(new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
            }
        });
    }

    private void updateNavDrawerHeader() {
        ClubDto club = LocationCheckinHelper.getInstance().getCurrentClub();

        TextView txtClubName = (TextView) mNavDrawerHeaderView.findViewById(R.id.txtClubName);
        View imgCheckOut = mNavDrawerHeaderView.findViewById(R.id.imgCheckOut);

        if(club != null && club.getId() != null && club.getId().length() > 0) {
            txtClubName.setText(club.getTitle());
            txtClubName.setTag(club.getId());
            imgCheckOut.setTag(club.getId());
            imgCheckOut.setOnClickListener(MainActivity.this);

            imgCheckOut.setVisibility(View.VISIBLE);
            txtClubName.setVisibility(View.VISIBLE);

            mNavDrawerHeaderView.findViewById(R.id.holderCheckOut).setVisibility(View.VISIBLE);
        } else {
            txtClubName.setText("");
            imgCheckOut.setTag(null);
            txtClubName.setTag(null);

            mNavDrawerHeaderView.findViewById(R.id.holderCheckOut).setVisibility(View.GONE);
        }
    }

    private void displayView(final int position) {
        if(position == NavDrawerData.SETTINGS_POSITION) {
            mCurrentFragment = new SettingsFragment();
        } else {
            mCurrentFragment = fragmentMap.get(position);
        }

        if (mCurrentFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();

            mFragmentTransaction.replace(R.id.frame_container, mCurrentFragment);
            mFragmentTransaction.commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);

        }
    }

    private void onShareClicked() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.share_to)));
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

    private void onMenuAddClicked() {
        Intent intent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(intent);
    }

    private void onChatBtnClicked() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        onInnerFragmentDestroyed();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new MessagesFragment()).commit();
    }

    private void onNavDrawerHeaderClicked() {
        mDrawerLayout.closeDrawer(mDrawerList);

        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivityForResult(intent, EditProfileActivity.REQUEST_CODE);
    }

    private void onImgCheckOutClicked() {
        showMessageDialog(
                getString(R.string.check_out),
                getString(R.string.are_you_sure_you_want_to_check_out),
                getString(R.string.check_out),
                getString(R.string.cancel)
        );
    }

    private void doCheckOut() {
        if(!NetworkUtils.isOn(MainActivity.this)) {
            Toast.makeText(MainActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog(getString(R.string.checking_out));

        LocationCheckinHelper.getInstance().checkOut(MainActivity.this, new CheckInOutCallbackInterface() {
            @Override
            public void onCheckInOutFinished(boolean isUserCheckOut) {
                handleCheckInCheckOutResults(isUserCheckOut);
            }
        });
    }

    private void onClubClicked(String clubId) {
        mDrawerLayout.closeDrawer(mDrawerList);

        Fragment fragment = ClubFragment.newInstance(null, clubId);

        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.replace(R.id.frame_container, fragment, ClubFragment.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    private void handleCheckInCheckOutResults(boolean result) {
        if(isFinishing()) {
            return;
        }

        if (result) {
            mNavDrawerHeaderView.findViewById(R.id.holderCheckOut).setVisibility(View.GONE);
            hideProgressDialog();

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(ClubFragment.TAG);
            if(fragment != null && fragment instanceof ClubFragment) {
                ClubFragment clubFragment = (ClubFragment) fragment;
                clubFragment.onClubCheckedOut();
            }
        } else {
            Toast.makeText(MainActivity.this, R.string.problem_occurred_please_try_again, Toast.LENGTH_SHORT).show();
        }
    }

    public MenuItem getMenuItemByIndex(int index) {
        if(index == MENU_ITEM_ADD_FRIEND) {
            return mMenuAddFriend;
        } else {
            return mMenuMessages;
        }
    }

    private Callback callback = new Callback() {
        @Override
        public void connectCallback(String channel, Object message) {
            L.d("SUBSCRIBE : CONNECT on channel:" + channel + " : " + message.getClass() + " : " + message.toString());
        }

        @Override
        public void disconnectCallback(String channel, Object message) {
            L.d("SUBSCRIBE : DISCONNECT on channel:" + channel + " : " + message.getClass() + " : " + message.toString());
        }

        public void reconnectCallback(String channel, Object message) {
            L.d("SUBSCRIBE : RECONNECT on channel:" + channel + " : " + message.getClass() + " : " + message.toString());
        }

        @Override
        public void successCallback(String channel, final Object message) {
//            L.d("SUBSCRIBE : " + channel + " : " + message.getClass() + " : " + message.toString());
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
            L.d("SUBSCRIBE : ERROR on channel " + channel + " : " + error.toString());
        }
    };

    private BroadcastReceiver mCheckInCheckOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNavDrawerHeader();
        }
    };
}

