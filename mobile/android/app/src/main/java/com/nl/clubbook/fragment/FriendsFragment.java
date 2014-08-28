package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            initActionBarTitle(getString(R.string.friend_list));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFriendsPagerAdapter.clearFragments();
    }

    @Override
    public void onTabChanged(String tabId) {
        String friends = getString(R.string.friends);
        mViewPager.setCurrentItem(friends.equalsIgnoreCase(tabId) ? FriendsPagerAdapter.INDEX_FRIENDS_LIST :
                FriendsPagerAdapter.INDEX_PENDING_FRIENDS);
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
        View view = getView();
        if(view == null) {
            return;
        }

        //init TabHost
        mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabHost.addTab(newTabSpec(mTabHost, getString(R.string.friends)));
        mTabHost.addTab(newTabSpec(mTabHost, getString(R.string.pending)));

        mTabHost.setOnTabChangedListener(this);

        //init ViewPager
        mFriendsPagerAdapter = new FriendsPagerAdapter(getChildFragmentManager(), FriendsFragment.this);
        mViewPager = (ViewPager)view.findViewById(R.id.viewPager);
        mViewPager.setAdapter(mFriendsPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    private TabHost.TabSpec newTabSpec(TabHost tabHost, String tabIndicator) {
        View tabIndicatorView = LayoutInflater.from(getActivity()).inflate(R.layout.apptheme_tab_indicator_holo, tabHost.getTabWidget(), false);
        TextView title = (TextView) tabIndicatorView.findViewById(android.R.id.title);
        title.setText(tabIndicator);

        return tabHost.newTabSpec(tabIndicator).setContent(android.R.id.tabcontent).setIndicator(tabIndicatorView);
    }
}
