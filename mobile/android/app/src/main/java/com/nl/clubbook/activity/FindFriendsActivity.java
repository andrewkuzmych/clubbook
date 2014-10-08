package com.nl.clubbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.nl.clubbook.R;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.L;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnInviteListener;
import com.sromku.simple.fb.listeners.OnLoginListener;

import java.util.List;

/**
 * Created by Volodymyr on 07.10.2014.
 */
public class FindFriendsActivity extends BaseActivity implements View.OnClickListener {

    private SimpleFacebook mSimpleFacebook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_find_friends);

        initActionBar(R.string.find_friends);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
            case R.id.txtInviteFriends:
                onInviteFriendsClicked();
                break;
        }
    }

    private void initView() {
        findViewById(R.id.txtInviteFriends).setOnClickListener(this);

    }

    private void onInviteFriendsClicked() {
        SessionManager sessionManager = getSession();
        if(sessionManager.isLoggedInByFacebook() || mSimpleFacebook.isLogin()) {
            sendFacebookRequest();
        } else {
            mSimpleFacebook.login(mOnLoginListener);
        }
    }

    private void sendFacebookRequest() {
        mSimpleFacebook.invite("Test message", new OnInviteListener() {
                    @Override
                    public void onComplete(List<String> strings, String s) {
                        L.w("onComplete");
                    }

                    @Override
                    public void onCancel() {
                        L.w("onCancel");
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        L.e("onException - " + throwable.getCause());
                    }

                    @Override
                    public void onFail(String s) {
                        L.e("onFail - " + s);
                    }
                }, "");
    }

    private OnLoginListener mOnLoginListener = new OnLoginListener() {

        @Override
        public void onFail(String reason) {
            showToast(R.string.something_went_wrong_please_try_again);
        }

        @Override
        public void onException(Throwable throwable) {
            showToast(R.string.something_went_wrong_please_try_again);
        }

        @Override
        public void onThinking() {
        }

        @Override
        public void onLogin() {
            sendFacebookRequest();
        }

        @Override
        public void onNotAcceptingPermissions(Permission.Type type) {
        }
    };
}
