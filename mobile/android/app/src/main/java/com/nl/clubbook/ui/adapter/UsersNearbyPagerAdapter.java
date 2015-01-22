package com.nl.clubbook.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.nl.clubbook.ui.fragment.UsersNearbyGridFragment;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by User on 25.12.2014.
 */
public class UsersNearbyPagerAdapter extends FragmentPagerAdapter {

    public static final int ITEMS_COUNT = 2;
    public static final int INDEX_ALL = 0;
    public static final int INDEX_CHECKED_IN = 1;

    private Map<Integer, Fragment> mFragments = new LinkedHashMap<Integer, Fragment>();
    private Fragment mTargetFragment;
    private final String[] mTitles;

    public UsersNearbyPagerAdapter(Fragment targetFragment, FragmentManager fm, String[] titles) {
        super(fm);

        mTargetFragment = targetFragment;
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int index) {
        Fragment fragment;

        switch (index) {
            case INDEX_ALL:
                fragment = UsersNearbyGridFragment.newInstance(mTargetFragment, UsersNearbyGridFragment.RequestTypes.AROUND);
                break;
            case INDEX_CHECKED_IN:
                fragment = UsersNearbyGridFragment.newInstance(mTargetFragment, UsersNearbyGridFragment.RequestTypes.CHECKED_IN);
                break;
            default:
                fragment = UsersNearbyGridFragment.newInstance(mTargetFragment, UsersNearbyGridFragment.RequestTypes.AROUND);
                break;
        }

        mFragments.put(index, fragment);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        mFragments.remove(position);
    }

    @Override
    public int getCount() {
        return ITEMS_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    public void refreshFragments() {
        Fragment baseAllUsersFragment = mFragments.get(INDEX_ALL);
        if(baseAllUsersFragment instanceof UsersNearbyGridFragment) {
            UsersNearbyGridFragment allUsersFragment = (UsersNearbyGridFragment) baseAllUsersFragment;
            allUsersFragment.refreshFragment();
        }

        Fragment baseCheckedInFragment = mFragments.get(INDEX_CHECKED_IN);
        if(baseCheckedInFragment instanceof UsersNearbyGridFragment) {
            UsersNearbyGridFragment checkedInFragment = (UsersNearbyGridFragment) baseCheckedInFragment;
            checkedInFragment.refreshFragment();
        }
    }
}
