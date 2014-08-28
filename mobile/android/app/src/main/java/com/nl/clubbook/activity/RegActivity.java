package com.nl.clubbook.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.AlertDialogManager;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.ImageUploader;
import com.nl.clubbook.helper.UiHelper;
import com.nl.clubbook.helper.UserEmailFetcher;
import com.nl.clubbook.helper.Validator;
import com.nl.clubbook.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by Andrew on 5/26/2014.
 */
public class RegActivity extends BaseDateActivity implements View.OnClickListener {

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
            case R.id.editBirthDate:
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mBirthDate.setTime(mCalendar.getTimeInMillis());

        EditText editBirthDate = (EditText) findViewById(R.id.editBirthDate);
        editBirthDate.setText(mDisplayFormat.format(mBirthDate));
    }

    private void initView() {
        findViewById(R.id.editBirthDate).setOnClickListener(this);
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
                L.d("avatar is selected: " + imageObj.getString("public_id"));

                new AsyncTask<Void, Void, Drawable>() {

                    @Override
                    protected Drawable doInBackground(Void... params) {
                        Drawable buttonBg = null;

                        try {
                            String url = ImageHelper.getProfileImage(imageObj.getString("url"));
                            InputStream is = (InputStream) new URL(url).getContent();
                            buttonBg = Drawable.createFromStream(is, null);
                        } catch (IOException e) {
                            L.i("" + e);
                        } catch (JSONException e) {
                            L.i("" + e);
                        }

                        return buttonBg;
                    }

                    @Override
                    protected void onPostExecute(Drawable drawable) {
                        hideProgress(true);

                        if(drawable == null) {
                            showNoInternetActiity();
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

    private void onAvatarClicked() {
        final AlertDialog dialog = imageUploader.selectPhoto();
        dialog.show();
    }

    private void onBtnRegisterClicked() {
        if(!isDataValid()) {
            return;
        }

        //init view
        EditText editName = (EditText)findViewById(R.id.editName);
        EditText editEmail = (EditText)findViewById(R.id.editEmail);
        EditText editPassword= (EditText)findViewById(R.id.editPassword);

        Spinner spinGender = (Spinner)findViewById(R.id.spinGender);
        UiHelper.TextValuePair data = (UiHelper.TextValuePair) spinGender.getSelectedItem();

        Spinner spinCountry = (Spinner)findViewById(R.id.spinCountry);
        UiHelper.TextValuePair dataCountry = (UiHelper.TextValuePair) spinCountry.getSelectedItem();

        //get data
        String userName = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String gender = data.getValue();
        String country = dataCountry.getValue();
        String city = "Amsterdam";
        String bio = "Hi, I'm using Clubbook.";

        showProgress(getString(R.string.loading));

        // store data
        DataStore.regByEmail(userName, email, password, gender, mServerFormat.format(mBirthDate), country, city, bio, avatar, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                // show error
                if (failed) {
                    hideProgress(false);
                    alert.showAlertDialog(RegActivity.this, "Error", getString(R.string.no_connection));
                    return;
                }

                hideProgress(true);

                if (result == null) {
                    alert.showAlertDialog(RegActivity.this, "Error", getString(R.string.email_exists));
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
            alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.name_incorrect));
            return false;
        }

        EditText editEmail = (EditText) findViewById(R.id.editEmail);
        String email = editEmail.getText().toString().trim();
        if (!Validator.isEmailValid(email)) {
            alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.email_incorrect));
            return false;
        }

        EditText editPassword = (EditText) findViewById(R.id.editPassword);
        String password = editPassword.getText().toString().trim();
        if (password.trim().length() < 6) {
            alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.pass_incorrect));
            return false;
        }

        EditText editBirthDate = (EditText) findViewById(R.id.editBirthDate);
        String dob = editBirthDate.getText().toString().trim();
        if (dob.trim().length() < 6) {
            alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.dob_incorrect));
            return false;
        }

        if(avatar == null) {
            alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.avatar_incorrect));
            return false;
        }

        return true;
    }
}