package com.nl.clubbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.FriendsPagerAdapter;

public class FriendsFragment extends BaseFragment implements ViewPager.OnPageChangeListener,
        TabHost.OnTabChangeListener, PendingFriendsFragment.OnFriendRequestAcceptedListener {

    private ViewPager mViewPager;
    private FriendsPagerAdapter mFriendsPagerAdapter;
    private TabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_friends, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initActionBarTitle(getString(R.string.friend_list));
        initView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment findFriendsFragment = mFriendsPagerAdapter.getFragmentByIndex(FriendsPagerAdapter.INDEX_ADD_INVITE_FRIENDS);
        if(findFriendsFragment != null) {
            findFriendsFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            actionBar.setIcon(R.drawable.icon_play);
            actionBar.setTitle(R.string.friend_list);
        }
    }

    @Override
    public void onDestroyView() {
        //clear fragments in adapter
        mFriendsPagerAdapter.clearFragments();

        super.onDestroyView();
    }

    @Override
    public void onTabChanged(String tabId) {
        String friends = getString(R.string.friends);
        String requests = getString(R.string.requests);

        if(friends.equalsIgnoreCase(tabId)) {
            mViewPager.setCurrentItem(FriendsPagerAdapter.INDEX_FRIENDS_LIST);
        } else if(requests.equalsIgnoreCase(tabId)) {
            mViewPager.setCurrentItem(FriendsPagerAdapter.INDEX_PENDING_FRIENDS);
        } else {
            mViewPager.setCurrentItem(FriendsPagerAdapter.INDEX_ADD_INVITE_FRIENDS);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        mTabHost.setCurrentTab(i);
    }

    @Override
    public void onFriendRequestAccepted() {
        Fragment fragment = mFriendsPagerAdapter.getFragmentByIndex(FriendsPagerAdapter.INDEX_FRIENDS_LIST);

        if(fragment != null && fragment instanceof FriendListFragment) {
            FriendListFragment friendListFragment = (FriendListFragment) fragment;
            friendListFragment.loadData();
        }
    }

    private void initView() {
//        View view = getView(); //TODO
//        if(view == null) {
//            return;
//        }
//
//        //init TabHost
//        mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
//        mTabHost.setup();
//
//        mTabHost.addTab(newTabSpec(mTabHost, getString(R.string.friends)));
//        mTabHost.addTab(newTabSpec(mTabHost, getString(R.string.requests)));
//        mTabHost.addTab(newTabSpec(mTabHost, getString(R.string.add_plus)));
//
//        mTabHost.setOnTabChangedListener(this);
//
//        //init ViewPager
//        mFriendsPagerAdapter = new FriendsPagerAdapter(getChildFragmentManager(), FriendsFragment.this);
//        mViewPager = (ViewPager)view.findViewById(R.id.viewPager);
//        mViewPager.setOffscreenPageLimit(mFriendsPagerAdapter.getCount());
//        mViewPager.setAdapter(mFriendsPagerAdapter);
//        mViewPager.setOnPageChangeListener(this);
    }

//    private TabHost.TabSpec newTabSpec(TabHost tabHost, String tabIndicator) {
//        View tabIndicatorView = LayoutInflater.from(getActivity()).inflate(R.layout.apptheme_tab_indicator_holo, tabHost.getTabWidget(), false);
//        TextView title = (TextView) tabIndicatorView.findViewById(android.R.id.title);
//        title.setText(tabIndicator);
//
//        return tabHost.newTabSpec(tabIndicator).setContent(android.R.id.tabcontent).setIndicator(tabIndicatorView);
//    }
}
