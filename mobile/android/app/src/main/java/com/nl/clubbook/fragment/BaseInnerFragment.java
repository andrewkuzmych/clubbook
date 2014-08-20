package com.nl.clubbook.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Volodymyr on 20.08.2014.
 */
public class BaseInnerFragment extends BaseFragment {

    @Override
    public void onDestroy() {
        ActionBarActivity activity = (ActionBarActivity)getActivity();
        if(activity != null && activity instanceof BaseFragment.OnInnerFragmentDestroyedListener && !activity.isFinishing()) {
            BaseFragment.OnInnerFragmentDestroyedListener listener = (BaseFragment.OnInnerFragmentDestroyedListener)activity;
            listener.onInnerFragmentDestroyed();

            Fragment targetFragment = getTargetFragment();
            if(targetFragment != null) {
                FragmentTransaction fTransaction = activity.getSupportFragmentManager().beginTransaction();
                fTransaction.show(targetFragment).commitAllowingStateLoss();
            }
        }

        super.onDestroy();
    }
}
