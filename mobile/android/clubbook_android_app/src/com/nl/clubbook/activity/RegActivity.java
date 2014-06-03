package com.nl.clubbook.activity;

import android.app.Activity;
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
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.helper.UserEmailFetcher;
import com.nl.clubbook.helper.Validator;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andrew on 5/26/2014.
 */
public class RegActivity extends BaseActivity {

    EditText user_text, password_text, emeil_text, dob_text;
    TextView user_label, password_label, emeil_label, dob_label, gender_label;
    DatePicker date_picker;
    Spinner gender_spinner;
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    Button reg_button;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_reg);
        init();

        Typeface typefaceIntroText = Typeface.createFromAsset(getAssets(), "fonts/TITILLIUMWEB-REGULAR.TTF");
        Typeface typefaceIntroTextBold = Typeface.createFromAsset(getAssets(), "fonts/TITILLIUMWEB-BOLD.TTF");
        //final Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/azoft-sans.ttf");

        user_text = (EditText) findViewById(R.id.name_text);
        user_text.setTypeface(typefaceIntroText);
        password_text = (EditText) findViewById(R.id.password_text);
        password_text.setTypeface(typefaceIntroText);
        emeil_text = (EditText) findViewById(R.id.email_text);
        emeil_text.setTypeface(typefaceIntroText);
        dob_text =  (EditText) findViewById(R.id.dob_text);
        dob_text.setTypeface(typefaceIntroText);

        user_label = (TextView) findViewById(R.id.name_label);
        user_label.setTypeface(typefaceIntroTextBold);
        password_label = (TextView) findViewById(R.id.pass_label);
        password_label.setTypeface(typefaceIntroTextBold);
        emeil_label = (TextView) findViewById(R.id.email_label);
        emeil_label.setTypeface(typefaceIntroTextBold);
        dob_label =  (TextView) findViewById(R.id.dob_label);
        dob_label.setTypeface(typefaceIntroTextBold);
        gender_label =  (TextView) findViewById(R.id.gender_label);
        gender_label.setTypeface(typefaceIntroTextBold);

        emeil_text.setText(UserEmailFetcher.getEmail(RegActivity.this));
        //user_text.setText(UserEmailFetcher.getUsername(RegActivity.this));


        session = new SessionManager(getApplicationContext());

        //init gender
        gender_spinner = (Spinner)findViewById(R.id.gender);
        final GenderPair items[] = new GenderPair[2];
        items[0] = new GenderPair( "Male","male" );
        items[1] = new GenderPair( "Female","female" );
        ArrayAdapter<GenderPair> adapter =
                new ArrayAdapter<GenderPair>(
                        this,
                        android.R.layout.simple_spinner_item,
                        items );
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
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", 1990);
        args.putInt("month", 6);
        args.putInt("day", 15);
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getSupportFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dob_text.setText(String.format("%02d", dayOfMonth)  + "." + String.format("%02d", monthOfYear + 1) + "." + String.valueOf(year));
        }
    };

    private void setHandlers() {
        //final Typeface typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/azoft-sans-bold.ttf");
        final Typeface typefaceIntroTextBold = Typeface.createFromAsset(getAssets(), "fonts/TITILLIUMWEB-BOLD.TTF");
        reg_button = (Button) findViewById(R.id.reg_btn);
        reg_button.setTypeface(typefaceIntroTextBold );

        // Login button click event
        reg_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Validation
                String user_name = user_text.getText().toString().trim();
                if (user_name.trim().length() < 2 )
                {
                    alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.name_incorrect), false);
                    return;
                }

                String email = emeil_text.getText().toString().trim();
                if (!Validator.isEmailValid(email))
                {
                    alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.email_incorrect), false);
                    return;
                }

                String  password = password_text.getText().toString().trim();
                if (password.trim().length() < 6 )
                {
                    alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.pass_incorrect), false);
                    return;
                }

                String dob = dob_text.getText().toString().trim();
                if (dob.trim().length() < 6 )
                {
                    alert.showAlertDialog(RegActivity.this, "Login failed..", getString(R.string.dob_incorrect), false);
                    return;
                }

                GenderPair data = (GenderPair)gender_spinner.getSelectedItem();
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
                            session.createLoginSession(user.getId(), user.getName(), user.getEmail(), user.getGender(), user.getDob(), user.getAvatar());
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
        public GenderPair( String spinnerText, String value ) {
            this.spinnerText = spinnerText;
            this.value = value;
        }

        public String getSpinnerText() {
            return spinnerText;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            return spinnerText;
        }

        String spinnerText;
        String value;
    }
}