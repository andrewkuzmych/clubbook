package com.nl.clubbook.ui.activity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nl.clubbook.ClubbookApplication;
import com.nl.clubbook.R;
import com.nl.clubbook.model.ClubbookPreferences;
import com.nl.clubbook.ui.fragment.dialog.MessageDialog;
import com.nl.clubbook.ui.fragment.dialog.ProgressDialog;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 5/17/14
 * Time: 8:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseActivity extends ActionBarActivity {
    protected Tracker mTracker;

    private boolean isProgressShow = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTracker = ((ClubbookApplication)getApplicationContext()).getTracker();
    }

    public void sendScreenStatistic(int stringResourceId) {
        sendScreenStatistic(getString(stringResourceId));
    }

    public void sendScreenStatistic(String screenName) {
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.AppViewBuilder().build());
    }

    protected void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public boolean isProgressShow() {
        return isProgressShow;
    }

    public void showProgressDialog(String message) {
        if(isFinishing()) {
            return;
        }

        isProgressShow = true;

        Fragment progressDialog = ProgressDialog.newInstance(null, message);
        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.add(progressDialog, ProgressDialog.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    protected void hideProgressDialog() {
        if(isFinishing()) {
            return;
        }

        DialogFragment progressDialog = (DialogFragment) getSupportFragmentManager().findFragmentByTag(ProgressDialog.TAG);
        if(progressDialog != null) {
            progressDialog.dismissAllowingStateLoss();
        }
    }

    protected void showToast(int messageResourceId) {
        showToast(getString(messageResourceId));
    }

    protected void showToast(String message) {
        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    protected void initActionBar(int stringResourceId) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(stringResourceId);
    }

    protected void initActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(title != null ? title : "");
    }

    protected void showMessageDialog(String title, String message, String txtPosBtn, String txtNegBtn) {
        MessageDialog messageDialog = MessageDialog.newInstance(null, title, message, txtPosBtn, txtNegBtn);

        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.add(messageDialog, MessageDialog.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    protected void showMessageDialog(String title, String message) {
        MessageDialog messageDialog = MessageDialog.newInstance(null, title, message, null, null);

        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.add(messageDialog, MessageDialog.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    protected void loadData() {
    }
}