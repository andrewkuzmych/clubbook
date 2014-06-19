package com.nl.clubbook.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.control.DatePickerFragment;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.AlertDialogManager;
import com.nl.clubbook.helper.UserEmailFetcher;
import com.nl.clubbook.helper.Validator;

/**
 * Created by Andrew on 5/26/2014.
 */
public class RegActivity extends ImageUploadActivity {

    EditText user_text, password_text, email_text, dob_text;
    TextView user_label, password_label, email_label, dob_label, gender_label;
    Spinner gender_spinner;
    AlertDialogManager alert = new AlertDialogManager();
    Button reg_button;
    Button avatar_button;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_reg);

        init();

        //set styles
        user_text = (EditText) findViewById(R.id.name_text);
        user_text.setTypeface(typeface_regular);
        password_text = (EditText) findViewById(R.id.password_text);
        password_text.setTypeface(typeface_regular);
        email_text = (EditText) findViewById(R.id.email_text);
        email_text.setTypeface(typeface_regular);
        dob_text = (EditText) findViewById(R.id.dob_text);
        dob_text.setTypeface(typeface_regular);

        user_label = (TextView) findViewById(R.id.name_label);
        user_label.setTypeface(typeface_bold);
        password_label = (TextView) findViewById(R.id.pass_label);
        password_label.setTypeface(typeface_bold);
        email_label = (TextView) findViewById(R.id.email_label);
        email_label.setTypeface(typeface_bold);
        dob_label = (TextView) findViewById(R.id.dob_label);
        dob_label.setTypeface(typeface_bold);
        gender_label = (TextView) findViewById(R.id.gender_label);
        gender_label.setTypeface(typeface_bold);
        email_text.setText(UserEmailFetcher.getEmail(RegActivity.this));

        //init gender
        gender_spinner = (Spinner) findViewById(R.id.gender);
        final GenderPair items[] = new GenderPair[2];
        items[0] = new GenderPair("Male", "male");
        items[1] = new GenderPair("Female", "female");
        ArrayAdapter<GenderPair> adapter =
                new ArrayAdapter<GenderPair>(
                        this,
                        android.R.layout.simple_spinner_item,
                        items);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        gender_spinner.setAdapter(adapter);

        setHandlers();

        dob_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        avatar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // init image uploader
                final AlertDialog dialog = selectPhoto();
                dialog.show();
            }
        });
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
        final Typeface typefaceIntroTextBold = Typeface.createFromAsset(getAssets(), "fonts/TITILLIUMWEB-BOLD.TTF");
        reg_button = (Button) findViewById(R.id.reg_btn);
        reg_button.setTypeface(typefaceIntroTextBold);

        avatar_button = (Button) findViewById(R.id.avatar_btn);
        avatar_button.setTypeface(typefaceIntroTextBold);

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

                GenderPair data = (GenderPair) gender_spinner.getSelectedItem();
                String gender = data.getValue();
                showProgress("Loading...");
                DataStore.regByEmail(user_name, email, password, gender, dob, new DataStore.OnResultReady() {
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
                            getSession().createLoginSession(user.getId(), user.getName(), user.getEmail(), user.getGender(), user.getDob(), user.getAvatar());
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

    class GenderPair {
        String text;
        String value;

        public GenderPair(String text, String value) {
            this.text = text;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            return text;
        }
    }
}