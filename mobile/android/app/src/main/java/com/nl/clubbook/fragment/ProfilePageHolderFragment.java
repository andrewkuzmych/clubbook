package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nl.clubbook.R;

/**
 * Created by Volodymyr on 14.11.2014.
 */
public class ProfilePageHolderFragment extends BaseInnerFragment {

    public static final int OPEN_FROM_CHAT = 4000;
    public static final int OPEN_MODE_DEFAULT = 6000;

    private int mOpenMode = OPEN_MODE_DEFAULT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_profile_page_holder, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initActionBarTitle(getString(R.string.user_profile));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            initActionBarTitle(getString(R.string.user_profile));
        }
    }
}
