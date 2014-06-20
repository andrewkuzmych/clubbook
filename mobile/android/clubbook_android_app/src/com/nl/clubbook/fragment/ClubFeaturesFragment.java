package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nl.clubbook.R;

public class ClubFeaturesFragment extends BaseFragment {

    public ClubFeaturesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_club_features, container, false);

        return rootView;
    }
}
