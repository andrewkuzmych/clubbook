package com.nl.clubbook.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.nl.clubbook.utils.L;

/**
 * Created by Volodymyr on 20.08.2014.
 */
public class BaseInnerFragment extends BaseFragment {

    @Override
    public void onDestroy() {
        ActionBarActivity activity = (ActionBarActivity)getActivity();
        if(activity != null && activity instanceof BaseFragment.OnInnerFragmentDestroyedListener && !activity.isFinishing()) {

            FragmentManager fManager = activity.getSupportFragmentManager();
            if(fManager.getBackStackEntryCount() < 1) {
                BaseFragment.OnInnerFragmentDestroyedListener listener = (BaseFragment.OnInnerFragmentDestroyedListener) activity;
                listener.onInnerFragmentDestroyed();
            }

            Fragment targetFragment = getTargetFragment();
            if (targetFragment != null) {
                FragmentTransaction fTransaction = fManager.beginTransaction();
                fTransaction.show(targetFragment).commitAllowingStateLoss();
            }
        }

        super.onDestroy();
    }
}
