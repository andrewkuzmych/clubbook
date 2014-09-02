package com.nl.clubbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.nl.clubbook.fragment.dialog.MessageDialog;
import com.nl.clubbook.fragment.dialog.ProgressDialog;
import com.nl.clubbook.helper.SessionManager;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 5/17/14
 * Time: 8:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseActivity extends ActionBarActivity {
    private SessionManager mSession;

    private boolean isProgressShow = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSession = SessionManager.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        //EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    public boolean isProgressShow() {
        return isProgressShow;
    }

    public SessionManager getSession() {
        return mSession;
    }

    public String getCurrentUserId() {
        return mSession.getUserId();
    }

    public void showProgressDialog(String message) {
        isProgressShow = true;

        Fragment progressDialog = ProgressDialog.newInstance(null, message);
        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.add(progressDialog, ProgressDialog.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    public void hideProgressDialog(boolean showContent) {
        isProgressShow = false;

        hideProgressDialog();

        // there is error. Show error view.
        if (!showContent) {
            showNoInternetActivity();
        }
    }

    protected void hideProgressDialog() {
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

    protected void showNoInternetActivity() {
        Intent i = new Intent(getApplicationContext(), NoInternetActivity.class);
        startActivity(i);
        finish();
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