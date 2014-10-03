package com.nl.clubbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nl.clubbook.R;
import com.nl.clubbook.fragment.ImageViewFragment;

/**
 * Created by Volodymyr on 15.08.2014.
 */
public class ClubPhotoPagerAdapter extends FragmentPagerAdapter {

    private String[] mUrls;

    public ClubPhotoPagerAdapter(FragmentManager fm, String[] urls) {
        super(fm);

        mUrls = urls;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageViewFragment.newInstance(mUrls[position], R.drawable.ic_club_avatar_default);
    }

    @Override
    public int getCount() {
        return mUrls.length;
    }
}
