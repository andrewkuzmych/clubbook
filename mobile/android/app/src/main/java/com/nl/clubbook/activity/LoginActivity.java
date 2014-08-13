package com.nl.clubbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.Validator;

/**
 * Created by Andrew on 5/26/2014.
 */
public class LoginActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initActionBar(R.string.log_in);
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                TextView txtUserName = (TextView) findViewById(R.id.txtUsername);
                TextView txtPassword = (TextView) findViewById(R.id.txtPassword);

                String email = txtUserName.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if (!Validator.isEmailValid(email)) {
                    alert.showAlertDialog(LoginActivity.this, "Login failed..", getString(R.string.email_incorrect), false);
                    return;
                }

                if (password.trim().length() < 6) {
                    alert.showAlertDialog(LoginActivity.this, "Login failed..", getString(R.string.pass_incorrect), false);
                    return;
                }

                //login request
                showProgress("Loading...");
                DataStore.loginByEmail(email, password, new DataStore.OnResultReady() {
                    @Override
                    public void onReady(Object result, boolean failed) {
                        // show error
                        if (failed) {
                            hideProgress(false);
                            alert.showAlertDialog(LoginActivity.this, "Error", getString(R.string.no_connection), false);
                            return;
                        }

                        hideProgress(true);

                        if (result == null) {
                            alert.showAlertDialog(LoginActivity.this, "Error", "Incorrect credentials", false);
                        } else {
                            UserDto user = (UserDto) result;
                            getSession().createLoginSession(user);
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }
                    }
                });
            }
        });
    }
}