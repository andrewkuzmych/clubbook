package com.nl.clubbook.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nl.clubbook.ClubbookApplication;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.fragment.dialog.MessageDialog;
import com.nl.clubbook.fragment.dialog.ProgressDialog;
import com.nl.clubbook.helper.SessionManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.reflect.Field;

/**
 * Created by Andrew on 6/8/2014.
 */
public class BaseFragment extends Fragment {

    protected Tracker mTracker;
    protected Target mTarget;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTracker = ((ClubbookApplication)getActivity().getApplicationContext()).getTracker();
    }

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

    @Override
    public void onDestroyView() {
        if(mTarget != null) {
            Picasso.with(getActivity()).cancelRequest(mTarget);
        }

        super.onDestroyView();
    }

    public void sendScreenStatistic(int stringResourceId) {
        sendScreenStatistic(getString(stringResourceId));
    }

    public void sendScreenStatistic(String stringName) {
        mTracker.setScreenName(MainActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.AppViewBuilder().build());
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
        fTransaction.add(R.id.fragmentContainer, fragment);
        fTransaction.hide(this);
        fTransaction.addToBackStack(fragmentClass.getSimpleName());
        fTransaction.commitAllowingStateLoss();

        if(activity instanceof OnInnerFragmentOpenedListener) {
            OnInnerFragmentOpenedListener listener = (OnInnerFragmentOpenedListener)activity;
            listener.onInnerFragmentOpened();
        }
    }

    protected void openFromInnerFragment(Fragment fragment, Class fragmentClass) {
        ActionBarActivity activity = (ActionBarActivity)getActivity();

        FragmentTransaction fTransaction = activity.getSupportFragmentManager().beginTransaction();
        fTransaction.add(R.id.fragmentContainer, fragment);
        if(getTargetFragment() != null) {
            fTransaction.hide(getTargetFragment());
        }
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

    protected void showProgress(String message) {
        Fragment progressDialog = ProgressDialog.newInstance(null, message);
        FragmentTransaction fTransaction = getChildFragmentManager().beginTransaction();
        fTransaction.add(progressDialog, ProgressDialog.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    protected void hideProgress() {
        DialogFragment progressDialog = (DialogFragment) getChildFragmentManager().findFragmentByTag(ProgressDialog.TAG);
        if(progressDialog != null) {
            progressDialog.dismissAllowingStateLoss();
        }
    }

    protected void showMessageDialog(Fragment targetFragment, int mode, String title, String message, String txtPosBtn, String txtNegBtn) {
        MessageDialog messageDialog = MessageDialog.newInstance(targetFragment, mode, title, message, txtPosBtn, txtNegBtn);

        FragmentTransaction fTransaction = getChildFragmentManager().beginTransaction();
        fTransaction.add(messageDialog, MessageDialog.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    protected void showMessageDialog(Fragment targetFragment, String title, String message, String txtPosBtn, String txtNegBtn) {
        MessageDialog messageDialog = MessageDialog.newInstance(targetFragment, title, message, txtPosBtn, txtNegBtn);

        FragmentTransaction fTransaction = getChildFragmentManager().beginTransaction();
        fTransaction.add(messageDialog, MessageDialog.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    protected void showMessageDialog(String title, String message) {
        DialogFragment messageDialog = MessageDialog.newSimpleMessageDialog(title, message);

        FragmentTransaction fTransaction = getChildFragmentManager().beginTransaction();
        fTransaction.add(messageDialog, MessageDialog.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    protected void initTarget() {
        final ActionBarActivity activity = (ActionBarActivity) getActivity();

        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setIcon(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setIcon(R.drawable.ic_transparent);
            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        };
    }

    protected void showToast(int messageResourceId) {
        showToast(getString(messageResourceId));
    }

    protected void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnInnerFragmentDestroyedListener {
        public void onInnerFragmentDestroyed();
    }

    public interface OnInnerFragmentOpenedListener {
        public void onInnerFragmentOpened();
    }
}
