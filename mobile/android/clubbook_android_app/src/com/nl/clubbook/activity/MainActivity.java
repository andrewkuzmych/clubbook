package com.nl.clubbook.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.adapter.NavDrawerListAdapter;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.fragment.*;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.NotificationHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.model.NavDrawerItem;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 5/19/14
 * Time: 1:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends BaseActivity {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SimpleFacebook mSimpleFacebook;
    private static final int DEFAULT_VIEW = 1;

    private CharSequence mTitle;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    HashMap<Integer, BaseFragment> fragmentMap = new HashMap<Integer, BaseFragment>();

    Callback callback = new Callback() {
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

    @Override
    protected void loadData() {

        initNavigationMenu();

        updateMessagesCount();

        displayDefaultView();

    }

    private void initNavigationMenu() {
        // get user data from session
        HashMap<String, String> user = getSession().getUserDetails();

        String user_avatar_url = null;
        if (user.get(SessionManager.KEY_AVATAR) != null)
            user_avatar_url = ImageHelper.getUserAvatar(user.get(SessionManager.KEY_AVATAR));

        // initialize navigation menu
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mDrawerList.requestFocusFromTouch();

        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("");
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#391A3C")));

        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItems.add(new NavDrawerItem(user.get(SessionManager.KEY_NAME), user_avatar_url, true));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "0"));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(3, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(MainActivity.this, R.layout.drawer_list_item, navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);

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
                navigateBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logout() {
        getSession().logoutUser();
        mSimpleFacebook.logout(mOnLogoutListener);
        Intent in = new Intent(getApplicationContext(), MainLoginActivity.class);
        startActivity(in);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        View badgeMessages = MenuItemCompat.getActionView(menu.findItem(R.id.badgeMessages));
        RelativeLayout messagesActionBar = (RelativeLayout) badgeMessages.findViewById(R.id.messagesActionBar);
        final ImageButton actionbarChatButton = (ImageButton) messagesActionBar.findViewById(R.id.actionbarChatButton);
        final TextView actionbarChatCount = (TextView) messagesActionBar.findViewById(R.id.actionbarChatCount);

        actionbarChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer countOfNewMessages = Integer.parseInt(actionbarChatCount.getText().toString());
                countOfNewMessages += 1;
                actionbarChatCount.setText(String.valueOf(countOfNewMessages));
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            navigateBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void navigateBack() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            current_fragment.backButtonWasPressed();
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String user_id = user.get(SessionManager.KEY_ID);
        subscribeToChannel("message_" + user_id);
    }

    @Override
    public void onStop() {
        super.onStop();
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String user_id = user.get(SessionManager.KEY_ID);
        NotificationHelper.pubnub.unsubscribe("message_" + user_id);
    }

    public BaseFragment getCurrentFragment() {
        return current_fragment;
    }

    private void handleNotification(JSONObject messageJson) {
        try {
            if (getCurrentFragment() instanceof ChatFragment && messageJson.getString("type").equalsIgnoreCase("chat")) {
                ChatFragment chatFragment = (ChatFragment) getCurrentFragment();
                String userTo = messageJson.getString("user_to");
                String userFrom = messageJson.getString("user_from");
                SessionManager session = new SessionManager(this);
                if (session.getConversationListner() != null && session.getConversationListner().equalsIgnoreCase(userFrom + "_" + userTo)) {
                    chatFragment.addComment(messageJson.getString("msg"));
                } else {
                    updateMessagesCount();
                }

            } else {
                updateMessagesCount();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentFragment(BaseFragment current_fragment) {

        this.current_fragment = current_fragment;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    public void setDefoltTitle() {
        setTitle(navMenuTitles[DEFAULT_VIEW]);
    }

    private OnLogoutListener mOnLogoutListener = new OnLogoutListener() {
        @Override
        public void onLogout() {

        }

        @Override
        public void onThinking() {

        }

        @Override
        public void onException(Throwable throwable) {

        }

        @Override
        public void onFail(String reason) {

        }
    };

    private void displayDefaultView() {
        Intent in = getIntent();
        int displayView = DEFAULT_VIEW;
        if (in.hasExtra("type")) {
            if (in.getStringExtra("type").equalsIgnoreCase("chat"))
                displayView = 3;
        }

        displayView(displayView);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // if user not logged - navigate to login activity
        if (!getSession().isLoggedIn()) {
            Intent i = new Intent(getApplicationContext(), MainLoginActivity.class);
            startActivity(i);
            return;
        }

        init();

        mTitle = getTitle();
        fragmentMap.put(0, new ProfileFragment());
        fragmentMap.put(1, new HomeFragment());
        fragmentMap.put(2, new ClubFeaturesFragment());
        fragmentMap.put(3, new MessagesFragment());
        fragmentMap.put(4, new FriendsFragment());
        fragmentMap.put(5, new SettingsFragment());

        getSupportActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        loadData();
    }

    public void updateMessagesCount() {
        if (getCurrentFragment() instanceof MessagesFragment) {
            ((MessagesFragment) getCurrentFragment()).loadData(false);
        }

        // retrieve the count of not read messages and update UI
        DataStore.unread_messages_count(getCurrentUserId(), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (!failed) {
                    setMessageCount(Integer.parseInt((String) result));
                }
            }
        });
    }

    private void displayView(final int position) {
        // update the main content by replacing fragments
        Fragment fragment = fragmentMap.get(position);

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();


            mFragmentTransaction.replace(R.id.frame_container, fragment);
            mFragmentTransaction.commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }


    private void subscribeToChannel(String channel_name) {
        try {
            NotificationHelper.pubnub.subscribe(channel_name, callback);
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }
    }

    private void setMessageCount(Integer count) {
        if (count == 0) {
            navDrawerItems.get(3).setCounterVisibility(false);
            return;
        } else {
            navDrawerItems.get(3).setCounterVisibility(true);
            navDrawerItems.get(3).setCount(String.valueOf(count));
            adapter = new NavDrawerListAdapter(this, R.layout.drawer_list_item,
                    navDrawerItems);
            mDrawerList.setAdapter(adapter);
        }
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            view.setSelected(true);
            // display view for selected nav drawer item
            displayView(position);
        }
    }

}

