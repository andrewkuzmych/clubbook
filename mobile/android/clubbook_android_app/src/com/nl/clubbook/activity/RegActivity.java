package com.nl.clubbook.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.control.DatePickerFragment;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.AlertDialogManager;
import com.nl.clubbook.helper.ImageUploader;
import com.nl.clubbook.helper.UiHelper;
import com.nl.clubbook.helper.UserEmailFetcher;
import com.nl.clubbook.helper.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Andrew on 5/26/2014.
 */
public class RegActivity extends BaseActivity {

    EditText user_text, city_text, password_text, email_text, dob_text;
    Spinner gender_spinner, country_spinner;
    private ImageUploader imageUploader;
    AlertDialogManager alert = new AlertDialogManager();
    Button reg_button;
    ImageButton avatar_button;
    JSONObject avatar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_reg);

        init();

        //set styles
        user_text = (EditText) findViewById(R.id.name_text);
        user_text.setTypeface(typeface_regular);
//        city_text = (EditText) findViewById(R.id.name_text);
//        city_text.setTypeface(typeface_regular);
        password_text = (EditText) findViewById(R.id.password_text);
        password_text.setTypeface(typeface_regular);
        email_text = (EditText) findViewById(R.id.email_text);
        email_text.setTypeface(typeface_regular);
        dob_text = (EditText) findViewById(R.id.dob_text);
        dob_text.setTypeface(typeface_regular);
        email_text.setText(UserEmailFetcher.getEmail(RegActivity.this));

        imageUploader = new ImageUploader(this) {
            @Override
            public void startActivityForResultHolder(Intent intent, int requestCode) {
                startActivityForResult(intent, requestCode);
            }

            @Override
            public void onImageSelected(JSONObject imageObj) throws JSONException {
                Log.d("ImageUploadActivity", "avatar is selected: " + imageObj.getString("public_id"));

                InputStream is = null;
                try {
                    is = (InputStream) new URL(imageObj.getString("url")).getContent();
                    Drawable buttonBg = Drawable.createFromStream(is, null);
                    avatar_button.setBackgroundDrawable(buttonBg);
                    avatar = imageObj;

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        };

        //init gender
        gender_spinner = UiHelper.createGenderSpinner((Spinner) findViewById(R.id.gender), this, "male");
        country_spinner = UiHelper.createCountrySpinner((Spinner) findViewById(R.id.country), this, "");

        setHandlers();
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("year", 1990);
        args.putInt("month", 6);
        args.putInt("day", 15);
        date.setArguments(args);
        // Set Call back to capture selected date
        date.setCallBack(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                dob_text.setText(String.format("%02d", dayOfMonth) + "." + String.format("%02d", monthOfYear + 1) + "." + String.valueOf(year));
            }
        });
        date.show(getSupportFragmentManager(), "Date Picker");
    }

    private void setHandlers() {
        dob_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        avatar_button = (ImageButton) findViewById(R.id.avatar_btn);
        avatar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // init image uploader
                final AlertDialog dialog = imageUploader.selectPhoto();
                dialog.show();
            }
        });

        reg_button = (Button) findViewById(R.id.reg_btn);
        reg_button.setTypeface(typeface_regular);


        // Login button click event
        reg_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Validation
                String user_name = user_text.getText().toString().trim();
                if (user_name.trim().length() < 2) {
                    alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.name_incorrect), false);
                    return;
                }

                String email = email_text.getText().toString().trim();
                if (!Validator.isEmailValid(email)) {
                    alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.email_incorrect), false);
                    return;
                }

                String password = password_text.getText().toString().trim();
                if (password.trim().length() < 6) {
                    alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.pass_incorrect), false);
                    return;
                }

                String dob = dob_text.getText().toString().trim();
                if (dob.trim().length() < 6) {
                    alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.dob_incorrect), false);
                    return;
                }

                UiHelper.TextValuePair data = (UiHelper.TextValuePair) gender_spinner.getSelectedItem();
                String gender = data.getValue();

                UiHelper.TextValuePair dataCountry = (UiHelper.TextValuePair) country_spinner.getSelectedItem();
                String country = dataCountry.getValue();

                // String city = city_text.getText().toString().trim();
                String city = "Amsterdam";
                String bio = "Hi, I'm using Clubbook.";

                if(avatar == null) {
                    alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.avatar_incorrect), false);
                    return;
                }

                showProgress("Loading...");

                // store data
                DataStore.regByEmail(user_name, email, password, gender, dob, country, city, bio, avatar, new DataStore.OnResultReady() {
                    @Override
                    public void onReady(Object result, boolean failed) {
                        // show error
                        if (failed) {
                            hideProgress(false);
                            alert.showAlertDialog(RegActivity.this, "Error", getString(R.string.no_connection), false);
                            return;
                        }

                        hideProgress(true);

                        if (result == null) {
                            alert.showAlertDialog(RegActivity.this, "Error", getString(R.string.email_exists), false);
                        } else {
                            UserDto user = (UserDto) result;
                            // save user in session
                            getSession().createLoginSession(user);
                            // navigate to main page
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });

            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            navigateBack();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void navigateBack() {
        Intent intent = new Intent(this, MainLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageUploader.onActivityResult(requestCode, resultCode, data);
    }
}