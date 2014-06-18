package com.nl.clubbook.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.nl.clubbook.R;
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
    LinearLayout failedView;
    RelativeLayout mainView;
    //ProgressDialog dialog;
    View contentView;
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
        contentView.setVisibility(View.GONE);
        failedView.setVisibility(View.GONE);

        BaseActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                progressDialog = ProgressDialog.show(BaseActivity.this, string,
                        "Loading application View, please wait...", false, true);
            }
        });

    }

    public void hideProgress(boolean showContent) {
        if (showContent) {
            failedView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
        } else {
            failedView.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);

            // on no internet clear current fragment
            if(current_fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();

                mFragmentTransaction.remove(current_fragment);
                mFragmentTransaction.commit();
            }
        }

        BaseActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        });
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

    }

    protected void init() {
        setRetryLayout();
    }

    private void setRetryLayout() {
        mainView = (RelativeLayout) findViewById(R.id.main_layout);
        failedView = (LinearLayout) getLayoutInflater().inflate(R.layout.retry, null);
        contentView = findViewById(R.id.content_layout);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL | RelativeLayout.CENTER_VERTICAL);

        failedView.setLayoutParams(params);
        failedView.setGravity(Gravity.CENTER);
        failedView.setOrientation(LinearLayout.VERTICAL);
        mainView.addView(failedView);

        setBaseHandlers();
    }

    protected void loadData() {

    }

    private void setBaseHandlers() {
        // retry button handler
        Button retry_button = (Button) findViewById(R.id.retry_button);
        retry_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadData();
            }
        });

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
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