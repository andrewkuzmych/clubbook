package com.nl.clubbook.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.User;
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
        setContentView(R.layout.ac_login);

        sendScreenStatistic(R.string.login_screen_android);

        setupToolBar();
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

        EditText editEmail = (EditText) findViewById(R.id.editEmail);
        EditText editPassword = (EditText) findViewById(R.id.editPassword);

        editEmail.addTextChangedListener(getTextWatcher(editEmail));
        editPassword.addTextChangedListener(getTextWatcher(editPassword));
    }

    private void onBtnLoginClicked() {
        EditText editEmail = (EditText) findViewById(R.id.editEmail);
        EditText editPassword = (EditText) findViewById(R.id.editPassword);

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (!Validator.isEmailValid(email)) {
            editEmail.setError(getString(R.string.email_incorrect));
            return;
        }

        if (password.trim().length() < RegActivity.MIN_PASSWORD_LENGTH) {
            editPassword.setError(getString(R.string.password_is_too_short));
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
                    if (result == null) {
                        showMessageDialog(getString(R.string.login_failed), getString(R.string.incorrect_credentials));
                    } else {
                        showToast(R.string.something_went_wrong_please_try_again);
                    }
                    return;
                }

                User user = (User) result;
                getSession().createLoginSession(user);

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

                sendBroadcast(new Intent(MainLoginActivity.ACTION_CLOSE_ACTIVITY));

                finish();
            }
        });
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