package com.nl.clubbook.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.fragment.BaseFragment;
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
    private ProgressDialog progressDialog;
    protected BaseFragment current_fragment;

    private SessionManager session;

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

    // styles
    public Typeface typeface_regular;
    public Typeface typeface_bold;

    public void showProgress(final String string) {
        BaseActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                progressDialog = new ProgressDialog(BaseActivity.this, R.style.ThemeDialog);
                //progressDialog.setTitle("Please wait");
                progressDialog.setMessage("loading application view, please wait...");
                progressDialog.show();
            }
        });
    }

    public void hideProgress(boolean showContent) {
        // hide progress
        BaseActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        });

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

    protected void navigateBack() {

    }

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

        typeface_regular = Typeface.createFromAsset(getAssets(), "fonts/TITILLIUMWEB-REGULAR.TTF");
        typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/TITILLIUMWEB-BOLD.TTF");

        // set context to use from DataStore for all app
        DataStore.setContext(this);
    }

    protected void init() {
    }

    protected void loadData() {
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
}