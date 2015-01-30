package com.nl.clubbook.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nl.clubbook.model.data.Place;
import com.nl.clubbook.ui.fragment.ClubFavoriteSettingsFragment;
import com.nl.clubbook.ui.fragment.ClubInfoFragment;
import com.nl.clubbook.ui.fragment.ClubNewsFragment;
import com.nl.clubbook.ui.fragment.ClubPhotosFragment;

/**
 * Created by Volodymyr on 17.09.2014.
 */
public class ClubInfoPagerAdapter extends FragmentPagerAdapter {

    public static final int INDEX_CLUB_INFO = 0;
    public static final int INDEX_NEWS = 1;
    public static final int INDEX_PHOTOS = 2;
    public static final int INDEX_FAVORITE = 3;

    public static final int FRAGMENT_COUNT = 4;

    private String[] mTitles;
    private Place mPlace;

    public ClubInfoPagerAdapter(FragmentManager fm, String[] titles, Place place) {
        super(fm);

        mTitles = titles;
        mPlace = place;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;

        switch (i) {
            case INDEX_CLUB_INFO:
                fragment = ClubInfoFragment.newInstance(mPlace);
                break;
            case INDEX_NEWS:
                fragment = new ClubNewsFragment();
                break;
            case INDEX_PHOTOS:
                fragment = ClubPhotosFragment.newInstance(mPlace.getPhotos());
                break;
            case INDEX_FAVORITE:
                fragment = new ClubFavoriteSettingsFragment();
                break;

            default:
                fragment = ClubInfoFragment.newInstance(mPlace);
                break;
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
