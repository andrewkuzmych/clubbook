package com.nl.clubbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.helper.Validator;

/**
 * Created by Andrew on 5/26/2014.
 */
public class LoginActivity extends BaseActivity  {
    EditText txtUsername, txtPassword;
    /*TextView forgotPass;*/
    TextView email_label;
    TextView pass_label;
    // login button
    Button btnLogin;

    // Session Manager Class
    SessionManager session;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();


        final Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/TITILLIUMWEB-REGULAR.TTF");
        final Typeface typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/TITILLIUMWEB-BOLD.TTF");
        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Email, Password input text
        email_label = (TextView) findViewById(R.id.email_label);
        email_label.setTypeface(typeface_bold);
        pass_label = (TextView) findViewById(R.id.pass_label);
        pass_label.setTypeface(typeface_bold);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtUsername.setTypeface(typeface);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtPassword.setTypeface(typeface);
        setHandlers();
    }

    private void setHandlers() {
        // Login button
        final Typeface typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/TITILLIUMWEB-BOLD.TTF");
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setTypeface(typeface_bold);
        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
                String email = txtUsername.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if (!Validator.isEmailValid(email))
                {
                    alert.showAlertDialog(LoginActivity.this, "Login failed..", getString(R.string.email_incorrect), false);
                    return;
                }

                if (password.trim().length() < 6 )
                {
                    alert.showAlertDialog(LoginActivity.this, "Login failed..", getString(R.string.pass_incorrect), false);
                    return;
                }

                showProgress("Loading...");
                DataStore.loginByEmail(email, password, new DataStore.OnResultReady() {
                    @Override
                    public void onReady(Object result, boolean failed) {
                        // show error
                        if (failed)
                        {
                            hideProgress(false);
                            alert.showAlertDialog(LoginActivity.this, "Error", getString(R.string.no_connection), false);
                            return;
                        }

                        hideProgress(true);


                        if (result == null) {
                            alert.showAlertDialog(LoginActivity.this, "Error", "Incorrect credentials", false);
                        } else {
                            UserDto user = (UserDto) result;
                            session.createLoginSession(user.getId(), user.getName(), user.getEmail(), user.getGender(), user.getDob(), user.getAvatar());
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }
                    }
                });


            }
        });
    }
}