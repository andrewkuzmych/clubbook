package com.nl.clubbook.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.helper.SessionManager;

import java.util.HashMap;

/**
 * Created by Andrew on 6/8/2014.
 */
public class BaseFragment extends Fragment {
    BaseFragment provoiusFregment;

    public BaseFragment() {
    }

    public BaseFragment(BaseFragment provoiusFregment) {
        this.provoiusFregment = provoiusFregment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void backButtonWasPressed() {
        ((MainActivity) getActivity()).setCurrentFragment(provoiusFregment);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setCurrentFragment(this);

        if (!((MainActivity) getActivity()).getDrawerToggle().isDrawerIndicatorEnabled()) {
            ((MainActivity) getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(true);
            ((MainActivity) getActivity()).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    protected SessionManager getSession() {
        return ((BaseActivity) getActivity()).getSession();
    }

    protected String getCurrentUserId() {
        return ((BaseActivity) getActivity()).getCurrentUserId();
    }
}
