package com.nl.clubbook.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.NoInternetActivity;
import com.nl.clubbook.helper.SessionManager;

/**
 * Created by Andrew on 6/8/2014.
 */
public class BaseFragment extends Fragment {

    protected SessionManager getSession() {
        return ((BaseActivity) getActivity()).getSession();
    }

    protected String getCurrentUserId() {
        return ((BaseActivity) getActivity()).getCurrentUserId();
    }

    protected void openFragment(Fragment fragment, Class fragmentClass) {
        ActionBarActivity activity = (ActionBarActivity)getActivity();

        FragmentTransaction fTransaction = activity.getSupportFragmentManager().beginTransaction();
        fTransaction.add(R.id.frame_container, fragment);
        fTransaction.hide(this);
        fTransaction.addToBackStack(fragmentClass.getSimpleName());
        fTransaction.commitAllowingStateLoss();

        if(activity instanceof OnInnerFragmentOpenedListener) {
            OnInnerFragmentOpenedListener listener = (OnInnerFragmentOpenedListener)activity;
            listener.onInnerFragmentOpened();
        }
    }

    protected void initActionBarTitle(String title) {
        ActionBarActivity activity = (ActionBarActivity) getActivity();
        if(activity == null) {
            return;
        }

        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(title);
    }

    protected void showNoInternetActivity() {
        Intent intent = new Intent(getActivity(), NoInternetActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    protected void showProgress() {
        ((BaseActivity) getActivity()).showProgress("Loading...");
    }

    protected void hideProgress(boolean showContent) {
        ((BaseActivity) getActivity()).hideProgress(showContent);
    }

    public interface OnInnerFragmentDestroyedListener {
        public void onInnerFragmentDestroyed();
    }

    public interface OnInnerFragmentOpenedListener {
        public void onInnerFragmentOpened();
    }
}
