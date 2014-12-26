package com.nl.clubbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nl.clubbook.fragment.PlacesFragment;

/**
 * Created by User on 22.12.2014.
 */
public class GoingOutPagerAdapter extends FragmentPagerAdapter {

    public static final int ITEMS_COUNT = 3;
    public static final int INDEX_ALL = 0;
    public static final int INDEX_CLUBS = 1;
    public static final int INDEX_BARS = 2;

    private final String[] mTitles;

    public GoingOutPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);

        mTitles = titles;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case INDEX_ALL:
                return PlacesFragment.newInstance(PlacesFragment.Types.ALL);
            case INDEX_CLUBS:
                return PlacesFragment.newInstance(PlacesFragment.Types.CLUB);
            case INDEX_BARS:
                return PlacesFragment.newInstance(PlacesFragment.Types.BAR);
            default:
                return PlacesFragment.newInstance(PlacesFragment.Types.ALL);
        }
    }

    @Override
    public int getCount() {
        return ITEMS_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
