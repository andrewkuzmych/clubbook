package com.nl.clubbook.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.ImageUploader;
import com.nl.clubbook.helper.UiHelper;
import com.nl.clubbook.helper.UserEmailFetcher;
import com.nl.clubbook.helper.Validator;
import com.nl.clubbook.utils.NetworkUtils;
import com.nl.clubbook.utils.ParseUtils;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Andrew on 5/26/2014.
 */
public class RegActivity extends BaseDateActivity implements View.OnClickListener {

    public static final int MIN_PASSWORD_LENGTH = 6;

    private ImageUploader imageUploader;
    private JSONObject avatar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_reg);

        sendScreenStatistic(R.string.registration_screen_android);

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
        EditText editBirthDate = (EditText)findViewById(R.id.editBirthDate);
        editBirthDate.setOnClickListener(this);
        findViewById(R.id.imgAvatar).setOnClickListener(this);
        findViewById(R.id.btnRegister).setOnClickListener(this);

        EditText editEmail = (EditText) findViewById(R.id.editEmail);
        editEmail.setText(UserEmailFetcher.getEmail(RegActivity.this));

        UiHelper.createGenderSpinner((Spinner) findViewById(R.id.spinGender), this, getString(R.string.male));
        UiHelper.createCountrySpinner((Spinner) findViewById(R.id.spinCountry), this, getString(R.string.Netherlands));

        EditText editName = (EditText)findViewById(R.id.editName);
        EditText editPassword = (EditText)findViewById(R.id.editPassword);

        editName.addTextChangedListener(getTextWatcher(editName));
        editEmail.addTextChangedListener(getTextWatcher(editEmail));
        editPassword.addTextChangedListener(getTextWatcher(editPassword));
        editBirthDate.addTextChangedListener(getTextWatcher(editBirthDate));
    }

    private void initImageUploader() {
        imageUploader = new ImageUploader(this) {
            @Override
            public void startActivityForResultHolder(Intent intent, int requestCode) {
                startActivityForResult(intent, requestCode);
            }

            @Override
            public void onImageSelected(Bitmap bitmap) {
                if(bitmap != null) {
                    ImageView imgAvatar = (ImageView)findViewById(R.id.imgAvatar);
                    imgAvatar.setImageBitmap(bitmap);
                } else {
                    showToast(R.string.something_went_wrong_please_try_again);
                }
            }

            @Override
            public void onImageUploaded(final JSONObject imageObj) {
                if(imageObj == null) {
                    showToast(R.string.something_went_wrong_please_try_again);
                } else {
                    avatar = imageObj;
                    doRegistration();
                }
            }
        };
    }

    private void onAvatarClicked() {
        final AlertDialog dialog = imageUploader.selectPhoto();
        dialog.show();
    }

    private void onBtnRegisterClicked() {
        if(!NetworkUtils.isOn(RegActivity.this)) {
            showToast(R.string.no_connection);
            return;
        }

        if(!imageUploader.isImageSelected()) {
            showMessageDialog(getString(R.string.app_name), getString(R.string.avatar_incorrect));
            return;
        }

        if(!isDataValid()) {
            return;
        }

        imageUploader.uploadImage();
    }

    private void doRegistration() {
        if(!NetworkUtils.isOn(RegActivity.this)) {
            showToast(R.string.no_connection);
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
        String bio = getString(R.string.default_about_me);

        showProgressDialog(getString(R.string.loading));

        // store data
        DataStore.regByEmail(userName, email, password, gender, mServerFormat.format(mBirthDate), country, city, bio, avatar, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {

                hideProgressDialog();

                if (failed) {
                    if(result == null) {
                        showToast(R.string.something_went_wrong_please_try_again);
                    } else {
                        showMessageDialog(getString(R.string.error), getString(R.string.user_with_this_email_has_already_exist));
                    }
                    return;
                }

                UserDto user = (UserDto) result;
                getSession().createLoginSession(user);

                sendBroadcast(new Intent(MainLoginActivity.ACTION_CLOSE_ACTIVITY));

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean isDataValid() {
        EditText editName = (EditText) findViewById(R.id.editName);
        String userName = editName.getText().toString().trim();
        if (userName.trim().length() < 2) {
            editName.setError(getString(R.string.name_incorrect));
            return false;
        }

        EditText editEmail = (EditText) findViewById(R.id.editEmail);
        String email = editEmail.getText().toString().trim();
        if (!Validator.isEmailValid(email)) {
            editEmail.setError(getString(R.string.email_incorrect));
            return false;
        }

        EditText editPassword = (EditText) findViewById(R.id.editPassword);
        String password = editPassword.getText().toString().trim();
        if (password.trim().length() < MIN_PASSWORD_LENGTH) {
            editPassword.setError(getString(R.string.password_is_too_short));
            return false;
        }

        EditText editBirthDate = (EditText) findViewById(R.id.editBirthDate);
        String dob = editBirthDate.getText().toString().trim();
        if (dob.trim().length() < 6) {
            editBirthDate.setError(getString(R.string.dob_incorrect));
            return false;
        } else {
            String strAge = getAge(dob);
            int age = ParseUtils.parseInt(strAge);
            if(age < 18) {
                showMessageDialog(getString(R.string.app_name), getString(R.string.you_must_be_18_age_to_be_able_use_clubbook));
                return false;
            }
        }

        return true;
    }

    public TextWatcher getTextWatcher(final EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setError(null);
            }
        };
    }
}