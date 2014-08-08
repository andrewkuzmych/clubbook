package com.nl.clubbook.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.helper.SessionManager;

/**
 * Created by Andrew on 6/8/2014.
 */
public class BaseFragment extends Fragment {
    BaseFragment previousFragment;

    public BaseFragment() {
    }

    public BaseFragment(BaseFragment previousFragment) {
        this.previousFragment = previousFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void backButtonWasPressed() {
        ((MainActivity) getActivity()).setCurrentFragment(previousFragment);
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

    protected void openFragment(BaseFragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();

        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.replace(R.id.frame_container, fragment).commit();
    }

    protected void showProgress() {
        ((BaseActivity) getActivity()).showProgress("Loading...");
    }

    protected void hideProgress(boolean showContent) {
        ((BaseActivity) getActivity()).hideProgress(showContent);
    }
}
