package com.nl.clubbook.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nl.clubbook.R;

/**
 * Created by Volodymyr on 27.01.2015.
 */
public class ClubFavoriteSettingsFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_club_favorite_settings, null);
    }
}
