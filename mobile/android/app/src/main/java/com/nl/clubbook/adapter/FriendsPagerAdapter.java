package com.nl.clubbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.nl.clubbook.fragment.FindFriendsFragment;
import com.nl.clubbook.fragment.FriendListFragment;
import com.nl.clubbook.fragment.PendingFriendsFragment;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Volodymyr on 26.08.2014.
 */
public class FriendsPagerAdapter extends FragmentPagerAdapter {

    public static final int INDEX_FRIENDS_LIST = 0;
    public static final int INDEX_PENDING_FRIENDS = 1;
    public static final int INDEX_ADD_INVITE_FRIENDS = 2;

    public static final int FRAGMENT_COUNT = 3;

    private HashMap<Integer, Fragment> mFragments = new LinkedHashMap<Integer, Fragment>();
    private Fragment mTargetFragment;

    public FriendsPagerAdapter(FragmentManager fManager, Fragment targetFragment) {
        super(fManager);

        mTargetFragment = targetFragment;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {
            case INDEX_FRIENDS_LIST:
                fragment = FriendListFragment.newInstance(mTargetFragment);
                break;
            case INDEX_PENDING_FRIENDS:
                fragment = PendingFriendsFragment.newInstance(mTargetFragment);
                break;
            case INDEX_ADD_INVITE_FRIENDS:
                fragment = new FindFriendsFragment();
                break;

            default:
                fragment = FriendListFragment.newInstance(mTargetFragment);
        }

        mFragments.put(position, fragment);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

        mFragments.remove(position);
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    public Fragment getFragmentByIndex(int index) {
        return mFragments.get(index);
    }

    public void clearFragments() {
        mFragments.clear();
    }
}
