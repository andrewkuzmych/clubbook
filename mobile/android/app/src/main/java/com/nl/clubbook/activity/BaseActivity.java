package com.nl.clubbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.fragment.BaseFragment;
import com.nl.clubbook.fragment.dialog.ProgressDialog;
import com.nl.clubbook.helper.AlertDialogManager;
import com.nl.clubbook.helper.SessionManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 5/17/14
 * Time: 8:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseActivity extends ActionBarActivity {
    protected ImageLoader imageLoader;
    protected DisplayImageOptions options;
    protected AlertDialogManager alert = new AlertDialogManager();
    protected BaseFragment currentFragment;

    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSession(new SessionManager(getApplicationContext()));

        // init image loader
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_list_image)
                .showImageForEmptyUri(R.drawable.default_list_image)
                .showImageOnFail(R.drawable.default_list_image)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        // set context to use from DataStore for all app
        DataStore.setContext(this);
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

    public SessionManager getSession() {
        return session;
    }

    protected void setSession(SessionManager session) {
        this.session = session;
    }

    public String getCurrentUserId() {
        HashMap<String, String> user = getSession().getUserDetails();
        return user.get(SessionManager.KEY_ID);
    }

    public void showProgress(final String message) {
        Fragment progressDialog = ProgressDialog.newInstance(null, message);
        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.add(progressDialog, ProgressDialog.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    public void hideProgress(boolean showContent) {
        // hide progress
        DialogFragment progressDialog = (DialogFragment) getSupportFragmentManager().findFragmentByTag(ProgressDialog.TAG);
        if(progressDialog != null) {
            progressDialog.dismissAllowingStateLoss();
        }

        // there is error. Show error view.
        if (!showContent) {
            showNoInternet();
        }
    }

    private void showNoInternet() {
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

    protected void loadData() {
    }

}