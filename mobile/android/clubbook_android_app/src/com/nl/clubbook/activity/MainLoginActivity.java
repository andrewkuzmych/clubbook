package com.nl.clubbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import com.cloudinary.Cloudinary;
import com.facebook.Session;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.SessionManager;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 5/17/14
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainLoginActivity extends BaseActivity {

    private SimpleFacebook mSimpleFacebook;
    private Button button_fb_login;
    SessionManager session_manager;

    //TODO move it to global
    private String CLOUD_NAME = "ddsoyfjll";
    // Login listener
    private OnLoginListener mOnLoginListener = new OnLoginListener() {

        @Override
        public void onFail(String reason)
        {
        }

        @Override
        public void onException(Throwable throwable)
        {
        }

        @Override
        public void onThinking()
        {
        }

        @Override
        public void onLogin()
        {
            GetFacebookProfile();
        }

        @Override
        public void onNotAcceptingPermissions(Permission.Type type)
        {
            // toast(String.format("You didn't accept %s permissions", type.name()));
        }
    };

    private void GetFacebookProfile() {
        OnProfileListener onProfileListener = new OnProfileListener() {
            @Override
            public void onComplete(Profile profile)
            {
                UploadImageTask task = new UploadImageTask(profile);//
                task.execute(profile.getId());
            }
                /*
                 * You can override other methods here:
                 * onThinking(), onFail(String reason), onException(Throwable throwable)
                 */
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

    private void updateUserInfo(Profile profile, final String avatar) {
        final String fb_id = profile.getId();
        final String name = profile.getName();
        final String email = profile.getEmail();
        final String gender = profile.getGender();

        final String access_token = Session.getActiveSession().getAccessToken();
        // TODO request DOB permission from From Facebook
        final String finalDob = "";

        showProgress("Loading...");
        this.runOnUiThread(new Runnable() {
            public void run() {
                DataStore.loginByFb(name, email, fb_id, access_token, gender, finalDob, avatar, new DataStore.OnResultReady() {
                    @Override
                    public void onReady(Object result, boolean failed) {
                        hideProgress(false);
                        if (failed) {
                            alert.showAlertDialog(MainLoginActivity.this, "Error", "Incorrect credentials", false);
                        } else {
                            UserDto user = (UserDto) result;
                            session_manager.createLoginSession(user.getId(), user.getName(), user.getEmail(), user.getGender(), user.getDob());
                            Intent in = new Intent(getApplicationContext(),
                                    MainActivity.class);
                            startActivity(in);
                        }
                    }
                });
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_login);

        session_manager = new SessionManager(getApplicationContext());

        if (session_manager.isLoggedIn())
        {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }

        button_fb_login = (Button)findViewById(R.id.login_fb);
       // button_fb_login.setTypeface(typefaceIntroText);
        button_fb_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                loginToFacebook();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        //setUIState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loginToFacebook() {
        if (mSimpleFacebook.isLogin())
            GetFacebookProfile();
        else
            mSimpleFacebook.login(mOnLoginListener);
    }

    class UploadImageTask extends AsyncTask<String, Void, Profile> {

        Profile profile;
        String imageUrl;
        UploadImageTask(Profile profile)    {
            this.profile = profile;
            //tv = (TextView)findViewById(R.id.tv);
        }

        // Executed on a special thread and all your
        // time taking tasks should be inside this method
        @Override
        protected Profile doInBackground(String... params) {
            try {
                String fb_id = params[0];
                showProgress("Loading...");
                String fb_photo = "https://graph.facebook.com/" + fb_id + "/picture?width=700&height=700";
                Cloudinary cloudinary = new Cloudinary(getApplicationContext());

                cloudinary.uploader().upload(fb_photo, Cloudinary.asMap("public_id", fb_id, "format", "jpg"));
                imageUrl = "http://res.cloudinary.com/" + CLOUD_NAME + "/image/upload/" + fb_id + ".jpg"; //result.getString("url");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            //tv.setText("Running task....");
            //myData = myParser.getDataFromWeb();
            return this.profile;
        }

        // Executed on the UI thread after the
        // time taking process is completed
        @Override
        protected void onPostExecute(Profile result) {
            super.onPostExecute(result);
            hideProgress(false);
            updateUserInfo(this.profile, imageUrl);
            //tv.setText("Completed the task, and the result is : " + myData);
        }
    }
}