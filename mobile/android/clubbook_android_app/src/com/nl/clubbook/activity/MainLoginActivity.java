package com.nl.clubbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    // Login listener
    private OnLoginListener mOnLoginListener = new OnLoginListener() {

        @Override
        public void onFail(String reason)
        {
            //mTextStatus.setText(reason);
            //Log.w(TAG, "Failed to login");
        }

        @Override
        public void onException(Throwable throwable)
        {
            //mTextStatus.setText("Exception: " + throwable.getMessage());
            //Log.e(TAG, "Bad thing happened", throwable);
        }

        @Override
        public void onThinking()
        {
            // show progress bar or something to the user while login is
            // happening
            // mTextStatus.setText("Thinking...");
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

                final String fb_id = profile.getId();
                final String name = profile.getName();
                final String email = profile.getEmail();
                final String gender = profile.getGender();
                final String birthday = profile.getBirthday();
                final String hometown = profile.getHometown();
                String fb_city = null;
                try {
                    JSONObject hometown_json = new JSONObject(hometown);
                    fb_city = hometown_json.getString("name");

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed hometown: \"" + hometown + "\"");
                }

                String dob = "";

                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat formatTo = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date dobDate = format.parse(birthday);
                    dob = formatTo.format(dobDate);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }


                final String access_token = Session.getActiveSession().getAccessToken();
                showProgress("Loading...");
                final String finalDob = dob;
                final String finalFb_city = fb_city;
                MainLoginActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        //tring name, String email, String fb_id, String fb_access_token, String fb_token_expires, String gender, String dob
                        DataStore.loginByFb(name, email, fb_id, access_token, gender, finalDob, new DataStore.OnResultReady() {
                            @Override
                            public void onReady(Object result, boolean failed) {
                                // show error
                                hideProgress(false);
                                if (failed) {
                                    alert.showAlertDialog(MainLoginActivity.this, "Error", "Incorrect credentials", false);
                                } else {
                                    UserDto user = (UserDto) result;
                                    session_manager.createLoginSession(user.getId(), user.getName(), user.getEmail(), user.getGender(), user.getDob());
                                    //sendRequestDialog();
                                    Intent in = new Intent(getApplicationContext(),
                                            MainActivity.class);
                                    startActivity(in);
                                }
                            }
                        });
                    }
                });
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}