package com.nl.clubbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nl.clubbook.fragment.ClubInfoFragment;
import com.nl.clubbook.fragment.ClubWallFragment;

/**
 * Created by Volodymyr on 17.09.2014.
 */
public class ClubInfoPagerAdapter extends FragmentPagerAdapter {

    public static final int INDEX_CLUB_INFO = 0;
    public static final int INDEX_WALL = 1;

    public static final int FRAGMENT_COUNT = 2;

    private String[] mTitles;
    private String mJsonClub;

    public ClubInfoPagerAdapter(FragmentManager fm, String[] titles, String jsonClub) {
        super(fm);

        mTitles = titles;
        mJsonClub = jsonClub;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;

        if(i == INDEX_CLUB_INFO) {
            fragment = ClubInfoFragment.newInstance(mJsonClub);
        } else {
            fragment = ClubWallFragment.newInstance();
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }
}
