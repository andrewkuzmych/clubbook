package com.nl.clubbook.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nl.clubbook.model.data.User;
import com.nl.clubbook.ui.fragment.ProfileFragment;

import java.util.List;

/**
 * Created by Volodymyr on 17.11.2014.
 */
public class ProfilePagerAdapter extends FragmentPagerAdapter {

    private List<User> mUsers;

    public ProfilePagerAdapter(FragmentManager fm,  List<User> users) {
        super(fm);

        mUsers = users;
    }

    @Override
    public Fragment getItem(int position) {
        return ProfileFragment.newInstance(null, mUsers.get(position), ProfileFragment.OPEN_MODE_DEFAULT);
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }
}
