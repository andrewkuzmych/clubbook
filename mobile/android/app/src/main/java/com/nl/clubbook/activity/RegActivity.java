package com.nl.clubbook.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.control.DatePickerFragment;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.AlertDialogManager;
import com.nl.clubbook.helper.ImageHelper;
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
public class RegActivity extends BaseActivity implements View.OnClickListener {

    private ImageUploader imageUploader;
    private AlertDialogManager alert = new AlertDialogManager();
    private JSONObject avatar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_reg);

        initImageUploader();
        initActionBar(R.string.sing_up);
        initView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageUploader.onActivityResult(requestCode, resultCode, data);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editBirthday:
                showDatePicker();
                break;
            case R.id.imgAvatar:
                onAvatarClicked();
                break;
            case R.id.btnRegister:
                onBtnRegisterClicked();
                break;
        }
    }

    private void initView() {
        findViewById(R.id.editBirthday).setOnClickListener(this);
        findViewById(R.id.imgAvatar).setOnClickListener(this);
        findViewById(R.id.btnRegister).setOnClickListener(this);

        EditText editEmail = (EditText) findViewById(R.id.editEmail);
        editEmail.setText(UserEmailFetcher.getEmail(RegActivity.this));

        UiHelper.createGenderSpinner((Spinner) findViewById(R.id.spinGender), this, "male");
        UiHelper.createCountrySpinner((Spinner) findViewById(R.id.spinCountry), this, getString(R.string.Netherlands));
    }

    private void initImageUploader() {
        imageUploader = new ImageUploader(this) {
            @Override
            public void startActivityForResultHolder(Intent intent, int requestCode) {
                startActivityForResult(intent, requestCode);
            }

            @Override
            public void onImageSelected(final JSONObject imageObj) throws JSONException {
                Log.d("ImageUploadActivity", "avatar is selected: " + imageObj.getString("public_id"));

                new AsyncTask<Void, Void, Drawable>() {
                    @Override
                    protected Drawable doInBackground(Void... params) {
                        Drawable buttonBg = null;

                        try {
                            String url = ImageHelper.getProfileImage(imageObj.getString("url"));
                            InputStream is = (InputStream) new URL(url).getContent();
                            buttonBg = Drawable.createFromStream(is, null);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return buttonBg;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return buttonBg;
                        }

                        return buttonBg;
                    }

                    @Override
                    protected void onPostExecute(Drawable drawable) {
                        if(drawable == null) {
                            //TODO error, show message
                            return;
                        }

                        ImageView imgAvatar = (ImageView)findViewById(R.id.imgAvatar);
                        imgAvatar.setImageDrawable(drawable);
                        avatar = imageObj;
                    }
                }.execute();
            }
        };
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
                EditText editBirthday = (EditText) findViewById(R.id.editBirthday);
                editBirthday.setText(String.format("%02d", dayOfMonth) + "." + String.format("%02d", monthOfYear + 1) + "." + String.valueOf(year));
            }
        });
        date.show(getSupportFragmentManager(), "Date Picker");
    }

    private void onAvatarClicked() {
        final AlertDialog dialog = imageUploader.selectPhoto();
        dialog.show();
    }

    private void onBtnRegisterClicked() {
        //TODO
        if(!isDataValid()) {
            return;
        }

        //init view
        EditText editName = (EditText)findViewById(R.id.editName);
        EditText editEmail = (EditText)findViewById(R.id.editEmail);
        EditText editPassword= (EditText)findViewById(R.id.editPassword);
        EditText editBirthday = (EditText)findViewById(R.id.editBirthday);

        Spinner spinGender = (Spinner)findViewById(R.id.spinGender);
        UiHelper.TextValuePair data = (UiHelper.TextValuePair) spinGender.getSelectedItem();

        Spinner spinCountry = (Spinner)findViewById(R.id.spinCountry);
        UiHelper.TextValuePair dataCountry = (UiHelper.TextValuePair) spinCountry.getSelectedItem();

        //get data
        String userName = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String birthday = editBirthday.getText().toString().trim();
        String gender = data.getValue();
        String country = dataCountry.getValue();
        String city = "Amsterdam";
        String bio = "Hi, I'm using Clubbook.";

        showProgress("Loading...");

        // store data
        DataStore.regByEmail(userName, email, password, gender, birthday, country, city, bio, avatar, new DataStore.OnResultReady() {
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

    private boolean isDataValid() {
        EditText editName = (EditText) findViewById(R.id.editName);
        String userName = editName.getText().toString().trim();
        if (userName.trim().length() < 2) {
            alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.name_incorrect), false);
            return false;
        }

        EditText editEmail = (EditText) findViewById(R.id.editEmail);
        String email = editEmail.getText().toString().trim();
        if (!Validator.isEmailValid(email)) {
            alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.email_incorrect), false);
            return false;
        }

        EditText editPassword = (EditText) findViewById(R.id.editPassword);
        String password = editPassword.getText().toString().trim();
        if (password.trim().length() < 6) {
            alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.pass_incorrect), false);
            return false;
        }

        EditText editBirthday = (EditText) findViewById(R.id.editBirthday);
        String dob = editBirthday.getText().toString().trim();
        if (dob.trim().length() < 6) {
            alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.dob_incorrect), false);
            return false;
        }

        if(avatar == null) {
            alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.avatar_incorrect), false);
            return false;
        }

        return true;
    }
}