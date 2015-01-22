package com.nl.clubbook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.nl.clubbook.R;
import com.nl.clubbook.ui.adapter.FriendsPagerAdapter;

public class FriendsFragment extends BaseFragment implements PendingFriendsFragment.OnFriendRequestAcceptedListener {

    private FriendsPagerAdapter mFriendsPagerAdapter;

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

        //init ViewPager
        mFriendsPagerAdapter = new FriendsPagerAdapter(getChildFragmentManager(), FriendsFragment.this, getResources().getStringArray(R.array.friend_titles));
        ViewPager viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(mFriendsPagerAdapter.getCount());
        viewPager.setAdapter(mFriendsPagerAdapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
    }
}
