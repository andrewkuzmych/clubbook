package com.nl.clubbook.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.facebook.Session;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.fragment.SettingsFragment;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.utils.NetworkUtils;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 5/17/14
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainLoginActivity extends BaseActivity implements View.OnClickListener {

    public static final String ACTION_CLOSE_ACTIVITY = "ACTION_CLOSE_ACTIVITY";

    private SimpleFacebook mSimpleFacebook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ac_main_login);

        sendScreenStatistic(R.string.main_login_screen_android);

        registerReceiver(mCloseActivityReceiver, new IntentFilter(ACTION_CLOSE_ACTIVITY));

        // start to track user location
        if(!LocationCheckinHelper.getInstance().isLocationTrackerStarted()) {
            LocationCheckinHelper.getInstance().startSmartLocationTracker(this);
        }

        if (!LocationCheckinHelper.getInstance().isLocationEnabled(this)) {
            Intent i = new Intent(getApplicationContext(), NoLocationActivity.class);
            startActivity(i);
            finish();
            return;
        }


        if (getSession().isLoggedIn()) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
            return;
        }

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mCloseActivityReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtLoginFacebook:
                onBtnFacebookLoginClicked();
                break;
            case R.id.txtRegWithEmail:
                onBtnRegClicked();
                break;
            case R.id.txtLogin:
                onBtnLoginClicked();
                break;
            case R.id.txtTermsOfService:
                onTxtTermsOfServiceClicked();
                break;
        }
    }

    private void initView() {
        findViewById(R.id.txtLoginFacebook).setOnClickListener(MainLoginActivity.this);
        findViewById(R.id.txtRegWithEmail).setOnClickListener(MainLoginActivity.this);
        findViewById(R.id.txtLogin).setOnClickListener(MainLoginActivity.this);
        findViewById(R.id.txtTermsOfService).setOnClickListener(MainLoginActivity.this);
    }

    private void onBtnFacebookLoginClicked() {
        if(!NetworkUtils.isOn(MainLoginActivity.this)) {
            Toast.makeText(MainLoginActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mSimpleFacebook.isLogin()) {
            // get profile and update data on server
            getFacebookProfile();
        } else {
            // get token and profile and put in SimpleFacebook session
            mSimpleFacebook.login(mOnLoginListener);
        }
    }

    private void onBtnRegClicked() {
        Intent intent = new Intent(getApplicationContext(), RegActivity.class);
        startActivity(intent);
    }

    private void onTxtTermsOfServiceClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SettingsFragment.URL_TEMPS_OF_SERVICE));
        startActivity(Intent.createChooser(browserIntent, getString(R.string.open_in)));
    }

    private void onBtnLoginClicked() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void getFacebookProfile() {
        OnProfileListener onProfileListener = new OnProfileListener() {
            @Override
            public void onComplete(Profile profile) {
                UpdateProfileInfoTask task = new UpdateProfileInfoTask(profile);
                task.execute(profile.getId());
            }
        };

        Profile.Properties properties = new Profile.Properties.Builder()
                .add(Profile.Properties.ID)
                .add(Profile.Properties.NAME)
                .add(Profile.Properties.EMAIL)
                .add(Profile.Properties.GENDER)
                .add(Profile.Properties.BIRTHDAY)
                .add(Profile.Properties.HOMETOWN)
                .build();

        mSimpleFacebook.getProfile(properties, onProfileListener);
    }

    private void updateUserInfo(Profile profile, final JSONObject avatar) {
        final String fb_id = profile.getId();
        final String name = profile.getName().split(" ")[0];
        final String email = profile.getEmail();
        final String gender = profile.getGender();

        final String access_token = Session.getActiveSession().getAccessToken();

        // TODO request DOB permission from From Facebook
        final String finalDob = "";

        showProgressDialog(getString(R.string.loading));
        DataStore.loginByFb(name, email, fb_id, access_token, gender, finalDob, avatar, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                hideProgressDialog();
                if (failed) {
                    showMessageDialog(getString(R.string.app_name), getString(R.string.incorrect_credentials));
                } else {
                    UserDto user = (UserDto) result;
                    getSession().createLoginSession(user);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private class UpdateProfileInfoTask extends AsyncTask<String, Void, Profile> {
        Profile profile;
        JSONObject avatar;

        UpdateProfileInfoTask(Profile profile) {
            this.profile = profile;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog(getString(R.string.loading));
        }

        // Executed on a special thread and all your
        // time taking tasks should be inside this method
        @Override
        protected Profile doInBackground(String... params) {
            try {
                String fb_id = params[0];
                String fb_photo = "https://graph.facebook.com/" + fb_id + "/picture?width=700&height=700";
                Cloudinary cloudinary = new Cloudinary(getApplicationContext());
                avatar = cloudinary.uploader().upload(fb_photo, Cloudinary.asMap("format", "jpg", "overwrite", "false", "public_id", fb_id));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return this.profile;
        }

        @Override
        protected void onPostExecute(Profile result) {
            hideProgressDialog();
            updateUserInfo(this.profile, avatar);
        }
    }

    private OnLoginListener mOnLoginListener = new OnLoginListener() {

        @Override
        public void onFail(String reason) {

        }

        @Override
        public void onException(Throwable throwable) {
        }

        @Override
        public void onThinking() {
        }

        @Override
        public void onLogin() {
            getFacebookProfile();
        }

        @Override
        public void onNotAcceptingPermissions(Permission.Type type) {
            // toast(String.format("You didn't accept %s permissions", type.name()));
        }
    };

    private BroadcastReceiver mCloseActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}