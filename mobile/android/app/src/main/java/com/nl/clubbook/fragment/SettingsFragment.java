package com.nl.clubbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainLoginActivity;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLogoutListener;

public class SettingsFragment extends BaseFragment implements View.OnClickListener{

    private SimpleFacebook mSimpleFacebook;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.holderPushNotifications:
                onHolderPushNotificationsClicked();
                break;
            case R.id.txtPrivacyPolicy:
                onTxtPrivacyPolicyClicked();
                break;
            case R.id.txtTermsOfservice:
                onTxtTermsOfServiceClicked();
                break;
            case R.id.txtDeleteAccount:
                onTxtDeleteAccountClicked();
                break;
            case R.id.txtContact:
                onTxtContactClicked();
                break;
            case R.id.txtLogOut:
                onTxtLogOutClicked();
                break;
        }
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        view.findViewById(R.id.holderPushNotifications).setOnClickListener(this);
        view.findViewById(R.id.txtPrivacyPolicy).setOnClickListener(this);
        view.findViewById(R.id.txtTermsOfservice).setOnClickListener(this);
        view.findViewById(R.id.txtDeleteAccount).setOnClickListener(this);
        view.findViewById(R.id.txtContact).setOnClickListener(this);
        view.findViewById(R.id.txtLogOut).setOnClickListener(this);

        mSimpleFacebook = SimpleFacebook.getInstance(getActivity());
    }

    private void onHolderPushNotificationsClicked() {
        //TODO
    }

    private void onTxtPrivacyPolicyClicked() {
        //TODO
    }

    private void onTxtTermsOfServiceClicked() {
        //TODO
    }

    private void onTxtDeleteAccountClicked() {
        //TODO
    }

    private void onTxtContactClicked() {
        //TODO
    }

    private void onTxtLogOutClicked() {
        getSession().logoutUser();
        mSimpleFacebook.logout(mOnLogoutListener);
        Intent in = new Intent(getActivity(), MainLoginActivity.class);
        startActivity(in);
        getActivity().finish();
    }

    private OnLogoutListener mOnLogoutListener = new OnLogoutListener() {
        @Override
        public void onLogout() {

        }

        @Override
        public void onThinking() {

        }

        @Override
        public void onException(Throwable throwable) {

        }

        @Override
        public void onFail(String reason) {

        }
    };
}
