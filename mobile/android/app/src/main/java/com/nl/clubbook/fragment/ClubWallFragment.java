package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nl.clubbook.R;

/**
 * Created by Volodymyr on 17.09.2014.
 */
public class ClubWallFragment extends BaseFragment {

    public static Fragment newInstance() {
        Fragment fragment = new ClubWallFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_club_wall, null);
    }
}
