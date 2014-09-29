package com.nl.clubbook.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.utils.NetworkUtils;

/**
 * Created by Volodymyr on 18.09.2014.
 */
public class ResetPasswordActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_reset_password);

        sendScreenStatistic(R.string.reset_password_screen_android);

        initActionBar(R.string.reset_password);
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
        findViewById(R.id.txtResetPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetPasswordClicked();
            }
        });
    }

    private void onResetPasswordClicked() {
        EditText editOldPassword = (EditText) findViewById(R.id.editOldPassword);
        EditText editNewPassword = (EditText) findViewById(R.id.editNewPassword);
        EditText editConfirmNewPassword = (EditText) findViewById(R.id.editConfirmNewPassword);

        String oldPassword = editOldPassword.getText().toString().trim();
        String newPassword = editNewPassword.getText().toString().trim();
        String confirmNewPassword = editConfirmNewPassword.getText().toString().trim();

        if(oldPassword.length() < RegActivity.MIN_PASSWORD_LENGTH) {
            editOldPassword.setError(getString(R.string.current_password_is_too_short));
            return;
        }

        if(newPassword.length() < RegActivity.MIN_PASSWORD_LENGTH) {
            editNewPassword.setError(getString(R.string.new_password_is_too_short));
            return;
        }

        if(!newPassword.equalsIgnoreCase(confirmNewPassword)) {
            editConfirmNewPassword.setError(getString(R.string.you_must_enter_the_same_password_twice));
            return;
        }

        doResetPassword(oldPassword, newPassword);
    }

    private void doResetPassword(String oldPassword, String newPassword) {
        if(!NetworkUtils.isOn(ResetPasswordActivity.this)) {
            showToast(R.string.no_connection);
            return;
        }

        String accessToken = getSession().getAccessToken();

        showProgressDialog(getString(R.string.changing_password));
        DataStore.resetPassword(oldPassword, newPassword, accessToken, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if(isFinishing()) {
                    return;
                }

                hideProgressDialog();

                if(failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                } else {
                    showToast(R.string.password_changed);
                    finish();
                }
            }
        });
    }
}
