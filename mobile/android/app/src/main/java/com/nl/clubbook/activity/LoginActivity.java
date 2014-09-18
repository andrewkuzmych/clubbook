package com.nl.clubbook.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.Validator;
import com.nl.clubbook.utils.NetworkUtils;

/**
 * Created by Andrew on 5/26/2014.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private final String URL_FORGOT_PASSWORD = "http://clubbookapp.herokuapp.com/reset_pass";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sendScreenStatistic(R.string.login_screen_android);

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

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnLogin:
                onBtnLoginClicked();
                break;
            case R.id.txtForgotPassword:
                onForgotPasswordClicked();
                break;
        }
    }

    private void initView() {
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.txtForgotPassword).setOnClickListener(this);
    }

    private void onBtnLoginClicked() {
        TextView txtUserName = (TextView) findViewById(R.id.txtUsername);
        TextView txtPassword = (TextView) findViewById(R.id.txtPassword);

        String email = txtUserName.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (!Validator.isEmailValid(email)) {
            showMessageDialog(getString(R.string.app_name), getString(R.string.email_incorrect));
            return;
        }

        if (password.trim().length() < 6) {
            showMessageDialog(getString(R.string.app_name), getString(R.string.pass_incorrect));
            return;
        }

        if(!NetworkUtils.isOn(LoginActivity.this)) {
            showToast(R.string.no_connection);
            return;
        }

        doLoginRequest(email, password);
    }

    private void onForgotPasswordClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_FORGOT_PASSWORD));
        startActivity(Intent.createChooser(browserIntent, getString(R.string.open_in)));
    }

    private void doLoginRequest(String email, String password) {
        showProgressDialog(getString(R.string.loading));
        DataStore.loginByEmail(email, password, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                hideProgressDialog();
                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    return;
                }

                if (result == null) {
                    showMessageDialog(getString(R.string.login_failed), getString(R.string.incorrect_credentials));
                } else {
                    UserDto user = (UserDto) result;
                    getSession().createLoginSession(user);

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);

                    sendBroadcast(new Intent(MainLoginActivity.ACTION_CLOSE_ACTIVITY));

                    finish();
                }
            }
        });
    }
}