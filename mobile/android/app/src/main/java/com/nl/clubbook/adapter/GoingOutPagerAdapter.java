package com.nl.clubbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.nl.clubbook.fragment.PlacesFragment;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by User on 22.12.2014.
 */
public class GoingOutPagerAdapter extends FragmentPagerAdapter {

    public static final int ITEMS_COUNT = 3;
    public static final int INDEX_ALL = 0;
    public static final int INDEX_CLUBS = 1;
    public static final int INDEX_BARS = 2;

    private HashMap<Integer, Fragment> mFragments = new LinkedHashMap<Integer, Fragment>();
    private final Fragment mTargetFragment;
    private final String[] mTitles;

    public GoingOutPagerAdapter(FragmentManager fm, Fragment targetFragment, String[] titles) {
        super(fm);

        mTargetFragment = targetFragment;
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int index) {
        Fragment fragment;

        switch (index) {
            case INDEX_ALL:
                fragment = PlacesFragment.newInstance(mTargetFragment, PlacesFragment.Types.ALL);
                break;
            case INDEX_CLUBS:
                fragment = PlacesFragment.newInstance(mTargetFragment, PlacesFragment.Types.CLUB);
                break;
            case INDEX_BARS:
                fragment = PlacesFragment.newInstance(mTargetFragment, PlacesFragment.Types.BAR);
                break;
            default:
                fragment = PlacesFragment.newInstance(mTargetFragment, PlacesFragment.Types.ALL);
                break;
        }

        mFragments.put(index, fragment);

        return fragment;
    }

    @Override
    public int getCount() {
        return ITEMS_COUNT;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

        mFragments.remove(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    public void searchPlaces(String query) {
        Fragment fragment = mFragments.get(INDEX_ALL);
        if(fragment != null && fragment instanceof PlacesFragment) {
            PlacesFragment allPlacesFragment = (PlacesFragment) fragment;
            allPlacesFragment.doSearchPlaces();
        }
    }

    public void refreshAllPalcesFragment() {
        Fragment fragment = mFragments.get(INDEX_ALL);
        if(fragment != null && fragment instanceof PlacesFragment) {
            PlacesFragment allPlacesFragment = (PlacesFragment) fragment;
            allPlacesFragment.doRefresh();
        }
    }
}
