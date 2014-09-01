package com.nl.clubbook.fragment;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.NoInternetActivity;
import com.nl.clubbook.activity.NoLocationActivity;
import com.nl.clubbook.fragment.dialog.ProgressDialog;
import com.nl.clubbook.helper.SessionManager;

import java.lang.reflect.Field;

/**
 * Created by Andrew on 6/8/2014.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onDetach() {
        super.onDetach();

        //workaround for fixing crash "No Activity"
        //http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

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

    protected void showNoLocationActivity() {
        Intent intent = new Intent(getActivity(), NoLocationActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void showProgress(String message) {
        Fragment progressDialog = ProgressDialog.newInstance(null, message);
        FragmentTransaction fTransaction = getChildFragmentManager().beginTransaction();
        fTransaction.add(progressDialog, ProgressDialog.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    public void hideProgress(boolean isSuccessfully) {
        hideProgress();

        if (!isSuccessfully) {
            showNoInternetActivity();
        }
    }

    public void hideProgress() {
        DialogFragment progressDialog = (DialogFragment) getChildFragmentManager().findFragmentByTag(ProgressDialog.TAG);
        if(progressDialog != null) {
            progressDialog.dismissAllowingStateLoss();
        }
    }

    public interface OnInnerFragmentDestroyedListener {
        public void onInnerFragmentDestroyed();
    }

    public interface OnInnerFragmentOpenedListener {
        public void onInnerFragmentOpened();
    }
}
