package com.nl.clubbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainLoginActivity;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.fragment.dialog.MessageDialog;
import com.nl.clubbook.utils.NetworkUtils;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLogoutListener;

public class SettingsFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, MessageDialog.MessageDialogListener {

    public static final String URL_TEMPS_OF_SERVICE = "http://clubbookapp.herokuapp.com/terms";

    private final String FEEDBACK_EMAIL = "feedback@clubbook.com";
    private final String URL_PRIVACY_POLICY = "http://clubbookapp.herokuapp.com/privacy";

    private SimpleFacebook mSimpleFacebook;

    private final int MESSAGE_DIALOG_ACTION_DELETE_ACCOUNT = 9876;
    private final int MESSAGE_DIALOG_ACTION_LOG_OUT = 5432;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initActionBarTitle(getString(R.string.settings));
        initView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            initActionBarTitle(getString(R.string.settings));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.cbPushNotifications) {
            onCbPushCheckedChanged();
        }
    }

    @Override
    public void onPositiveButtonClick(MessageDialog dialogFragment) {
        switch (dialogFragment.getActionId()) {
            case MESSAGE_DIALOG_ACTION_DELETE_ACCOUNT:
                doDeleteAccount();
                break;
            case MESSAGE_DIALOG_ACTION_LOG_OUT:
                doLogOut();
                break;
        }
    }

    @Override
    public void onNegativeButtonClick(MessageDialog dialogFragment) {
        dialogFragment.dismissAllowingStateLoss();
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        CheckBox cbPushNotifications = (CheckBox) view.findViewById(R.id.cbPushNotifications);
        cbPushNotifications.setOnCheckedChangeListener(this);

        view.findViewById(R.id.txtPrivacyPolicy).setOnClickListener(this);
        view.findViewById(R.id.txtTermsOfservice).setOnClickListener(this);
        view.findViewById(R.id.txtDeleteAccount).setOnClickListener(this);
        view.findViewById(R.id.txtContact).setOnClickListener(this);
        view.findViewById(R.id.txtLogOut).setOnClickListener(this);

        mSimpleFacebook = SimpleFacebook.getInstance(getActivity());
    }

    private void onCbPushCheckedChanged() {
        //TODO
    }

    private void onTxtPrivacyPolicyClicked() {
        Fragment fragment = WebViewFragment.newInstance(SettingsFragment.this, getString(R.string.privacy_policy),
                URL_PRIVACY_POLICY);
        openFragment(fragment, WebViewFragment.class);
    }

    private void onTxtTermsOfServiceClicked() {
        Fragment fragment = WebViewFragment.newInstance(SettingsFragment.this, getString(R.string.terms_of_service),
                URL_TEMPS_OF_SERVICE);
        openFragment(fragment, WebViewFragment.class);
    }

    private void onTxtContactClicked() {
        Intent intentEmail = new Intent(Intent.ACTION_SEND);
        intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{FEEDBACK_EMAIL});
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.question_about_clubbook));
        intentEmail.setType("message/rfc822");
        startActivity(Intent.createChooser(intentEmail, getString(R.string.choose_an_email_provider)));
    }

    private void onTxtDeleteAccountClicked() {
        showMessageDialog(
                SettingsFragment.this,
                MESSAGE_DIALOG_ACTION_DELETE_ACCOUNT,
                getString(R.string.app_name),
                getString(R.string.are_you_sure_you_want_delete_your_account),
                getString(R.string.delete),
                getString(R.string.cancel)
        );
    }

    private void onTxtLogOutClicked() {
        showMessageDialog(
                SettingsFragment.this,
                MESSAGE_DIALOG_ACTION_LOG_OUT,
                getString(R.string.log_out),
                getString(R.string.are_you_sure_you_want_to_log_out),
                getString(R.string.log_out),
                getString(R.string.cancel)
        );
    }

    private void doDeleteAccount() {
        if(!NetworkUtils.isOn(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(getString(R.string.deleting_profile));

        String accessToken = getSession().getAccessToken();
        DataStore.deleteProfile(getActivity(), accessToken, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if(!failed) {
                    hideProgress();

                    doLogOut();

                    getActivity().finish();
                } else {
                    hideProgress();
                    Toast.makeText(getActivity(), R.string.something_went_wrong_please_try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void doLogOut() {
        getSession().logoutUser();
        mSimpleFacebook.logout(mOnLogoutListener);
        Intent intent = new Intent(getActivity(), MainLoginActivity.class);
        startActivity(intent);
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
