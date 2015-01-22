package com.nl.clubbook.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
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
import com.nl.clubbook.model.ClubbookPreferences;
import com.nl.clubbook.ui.adapter.NavDrawerItem;
import com.nl.clubbook.model.data.ChatMessage;
import com.nl.clubbook.model.httpclient.HttpClientManager;
import com.nl.clubbook.model.data.Place;
import com.nl.clubbook.model.data.JSONConverter;
import com.nl.clubbook.ui.fragment.BaseFragment;
import com.nl.clubbook.ui.fragment.ChatFragment;
import com.nl.clubbook.ui.fragment.ClubFragment;
import com.nl.clubbook.ui.fragment.FastCheckInFragment;
import com.nl.clubbook.ui.fragment.FriendsFragment;
import com.nl.clubbook.ui.fragment.GoingOutFragment;
import com.nl.clubbook.ui.fragment.MessagesFragment;
import com.nl.clubbook.ui.fragment.SettingsFragment;
import com.nl.clubbook.ui.fragment.UsersNearbyFragment;
import com.nl.clubbook.ui.fragment.YesterdayFragment;
import com.nl.clubbook.ui.fragment.dialog.MessageDialog;
import com.nl.clubbook.helper.CheckInOutCallbackInterface;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.helper.NotificationReceiver;
import com.nl.clubbook.helper.NotificationHelper;
import com.nl.clubbook.ui.drawer.NavDrawerData;
import com.nl.clubbook.ui.drawer.NavDrawerListAdapter;
import com.nl.clubbook.ui.view.CustomToolBar;
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

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private View mNavDrawerHeaderView;
    private ActionBarDrawerToggle mDrawerToggle;

    private TextView actionbarChatCount;
    private Integer chatCountOfNewMessages = 0;

    private HashMap<Integer, BaseFragment> fragmentMap = new HashMap<Integer, BaseFragment>();
    private List<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter mAdapter;
    private CustomToolBar mToolbar;

    private Fragment mCurrentFragment;
    private int mSelectedDrawerItem = 0;
    private boolean mShouldHandleDrawerClick = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);

        setupToolBar();
        initReceivers();

        ClubbookPreferences preferences = ClubbookPreferences.getInstance(getBaseContext());
        if (!preferences.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), MainLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        initFragment();
        initNavDrawer();
        initView();

        loadData();
        loadConfig();

        L.i("onCreate");
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
        L.e("onDestroy");

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
        } else if(mCurrentFragment != null && mCurrentFragment instanceof FriendsFragment) {
            mCurrentFragment.onActivityResult(requestCode, resultCode, data);
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
            case R.id.imgCheckOut:
                onImgCheckOutClicked();
                break;
            case R.id.txtClubName:
                onClubClicked();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mDrawerLayout.closeDrawer(mDrawerList);
        mSelectedDrawerItem = position;
        mShouldHandleDrawerClick = true;
    }

    @Override
    protected void loadData() {
        displayDefaultView();
    }

    @Override
    public void onInnerFragmentDestroyed() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mDrawerToggle.syncState();
    }

    @Override
    public void onInnerFragmentOpened() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setIsInBackMode(true);
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

        ClubbookPreferences.getInstance(getBaseContext()).logoutUser();

        LocationCheckinHelper.clear();

        Intent intent = new Intent(MainActivity.this, MainLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void initNavDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.listDrawer);
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                if(!mShouldHandleDrawerClick) {
                    return;
                }

                mShouldHandleDrawerClick = false;

                if(mSelectedDrawerItem == 0) { //header
                    onNavDrawerHeaderClicked();
                    return;
                }

                if(mSelectedDrawerItem - 1 == NavDrawerData.SHARE_POSITION) {
                    onShareClicked();
                    return;
                }

                displayView(mSelectedDrawerItem - 1);
            }

            public void onDrawerOpened(View drawerView) {
                sendScreenStatistic(R.string.sidebar_screen_android);
            }
        };
        navDrawerItems = NavDrawerData.getNavDrawerItems(MainActivity.this);

        // set adapter
        View navDrawerHeaderView = initNavDrawerHeader();
        mAdapter = new NavDrawerListAdapter(MainActivity.this, navDrawerItems);
        mDrawerList.addHeaderView(navDrawerHeaderView);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(MainActivity.this);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
    }

    private void initReceivers() {
        registerReceiver(mCheckInCheckOutReceiver, new IntentFilter(ACTION_CHECK_IN_CHECK_OUT));
    }

    private View initNavDrawerHeader() {
        mNavDrawerHeaderView = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_nav_drawer_header, null);

        fillNavDrawerHeader();

        return mNavDrawerHeaderView;
    }

    private void fillNavDrawerHeader() {
        ClubbookPreferences preferences = ClubbookPreferences.getInstance(getBaseContext());
        HashMap<String, String> user = preferences.getUserDetails();

        ImageView imgAvatar = (ImageView) mNavDrawerHeaderView.findViewById(R.id.imgAvatar);
        TextView txtProfileName = (TextView) mNavDrawerHeaderView.findViewById(R.id.txtProfileName);
        TextView txtProfileInfo = (TextView) mNavDrawerHeaderView.findViewById(R.id.txtProfileInfo);

        String userAvatarUrl = user.get(ClubbookPreferences.KEY_AVATAR);
        if (userAvatarUrl != null) {
            userAvatarUrl = ImageHelper.getUserAvatar(userAvatarUrl);
            Picasso.with(getBaseContext()).load(userAvatarUrl).error(R.drawable.ic_avatar_unknown).into(imgAvatar);
        }

        String profileName = user.get(ClubbookPreferences.KEY_NAME);
        String profileAge = user.get(ClubbookPreferences.KEY_AGE);
        String profileGender = user.get(ClubbookPreferences.KEY_GENDER);

        txtProfileName.setText(profileName != null ? profileName : "");
        txtProfileInfo.setText((profileAge != null && profileAge.length() > 0) ? profileAge + ", " : "");
        txtProfileInfo.append(profileGender != null ? profileGender : "");

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

                ClubbookPreferences preferences = ClubbookPreferences.getInstance(getBaseContext());
                if (preferences.getConversationListener() != null && preferences.getConversationListener().equalsIgnoreCase(userFrom + "_" + userTo)) {
                    ChatMessage lastMessage = JSONConverter.newChatMessage(data.optJSONObject("last_message"));
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
        ClubbookPreferences preferences = ClubbookPreferences.getInstance(getBaseContext());
        NotificationHelper.pubnub.unsubscribe("message_" + preferences.getUserId());
    }

    private void subscribePubnup() {
        ClubbookPreferences preferences = ClubbookPreferences.getInstance(getBaseContext());
        subscribeToChannel("message_" + preferences.getUserId());
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
        ClubbookPreferences preferences = ClubbookPreferences.getInstance(getBaseContext());
        HttpClientManager.getInstance().getNotifications(preferences.getAccessToken(), new HttpClientManager.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (!failed) {
                    setMessageCount(Integer.parseInt((String) result));
                }
            }
        });
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");

        mToolbar = (CustomToolBar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mToolbar.isInBackMode()) {
                    onBackPressed();
                    mToolbar.setIsInBackMode(false);
                    return;
                }

                if(mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    mDrawerLayout.openDrawer(mDrawerList);
                }

                mDrawerToggle.syncState();
            }
        });
    }

    private void initFragment() {
        fragmentMap.put(NavDrawerData.GOING_OUT_POSITION, new GoingOutFragment());
        fragmentMap.put(NavDrawerData.MESSAGES_POSITION, new MessagesFragment());
        fragmentMap.put(NavDrawerData.FAST_CHECK_IN, new FastCheckInFragment());
        fragmentMap.put(NavDrawerData.USERS_NEARBY, new UsersNearbyFragment());
        fragmentMap.put(NavDrawerData.YESTERDAY, new YesterdayFragment());
        fragmentMap.put(NavDrawerData.FRIENDS_POSITION, new FriendsFragment());
    }

    private void loadConfig() {
        if(!NetworkUtils.isOn(MainActivity.this)) {
            return;
        }

        HttpClientManager.getInstance().getConfig(getBaseContext(), new HttpClientManager.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
            }
        });
    }

    private void updateNavDrawerHeader() {
        Place place = LocationCheckinHelper.getInstance().getCurrentClub();

        TextView txtClubName = (TextView) mNavDrawerHeaderView.findViewById(R.id.txtClubName);
        View imgCheckOut = mNavDrawerHeaderView.findViewById(R.id.imgCheckOut);

        if(place != null && place.getId() != null && place.getId().length() > 0) {
            txtClubName.setText(place.getTitle());
            imgCheckOut.setTag(place.getId());
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

            mFragmentTransaction.replace(R.id.fragmentContainer, mCurrentFragment);
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

    private void onChatBtnClicked() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        onInnerFragmentDestroyed();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, new MessagesFragment()).commit();
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

    private void onClubClicked() {
        Place place = LocationCheckinHelper.getInstance().getCurrentClub();
        if(place == null) {
            return;
        }

        mDrawerLayout.closeDrawer(mDrawerList);

        Fragment fragment = ClubFragment.newInstance(null, place);

        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.replace(R.id.fragmentContainer, fragment, ClubFragment.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    private void handleCheckInCheckOutResults(boolean result) {
        if(isFinishing()) {
            return;
        }

        hideProgressDialog();

        if (result) {
            mNavDrawerHeaderView.findViewById(R.id.holderCheckOut).setVisibility(View.GONE);

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(ClubFragment.TAG);
            if(fragment != null && fragment instanceof ClubFragment) {
                ClubFragment clubFragment = (ClubFragment) fragment;
                clubFragment.onClubCheckedOut();
            }
        } else {
            Toast.makeText(MainActivity.this, R.string.problem_occurred_please_try_again, Toast.LENGTH_SHORT).show();
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

