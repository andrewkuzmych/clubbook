package com.nl.clubbook.fragment;

import android.app.Activity;
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
import com.nl.clubbook.activity.ResetPasswordActivity;
import com.nl.clubbook.datasource.HttpClientManager;
import com.nl.clubbook.fragment.dialog.MessageDialog;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.NetworkUtils;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLogoutListener;

public class SettingsFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, MessageDialog.MessageDialogListener {

    public static final String URL_TEMPS_OF_SERVICE = "http://clubbookapp.herokuapp.com/terms";

    private final String FEEDBACK_EMAIL = "support@clubbook.com";
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

        sendScreenStatistic(R.string.settings_screen_android);

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
            case R.id.txtTermsOfService:
                onTxtTermsOfServiceClicked();
                break;
            case R.id.txtDeleteAccount:
               onTxtDeleteAccountClicked();
                break;
            case R.id.txtContact:
                onTxtContactClicked();
                break;
            case R.id.txtResetPassword:
                onTxtResetPasswordClicked();
                break;
            case R.id.txtLogOut:
                onTxtLogOutClicked();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cbPushNotifications:
                onPushEnablingChanged((CheckBox) buttonView, isChecked);
                break;
            case R.id.cbPushVibrations:
                onPushVibrationCheckedChanged(isChecked);
                break;
            case R.id.cbUserVisibleNearby:
                onVisibleNearbyCheckedChanged((CheckBox) buttonView, isChecked);
                break;
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

        SessionManager session = getSession();

        CheckBox cbPushNotifications = (CheckBox) view.findViewById(R.id.cbPushNotifications);
        cbPushNotifications.setChecked(session.isNotificationEnabled());
        cbPushNotifications.setOnCheckedChangeListener(this);

        CheckBox cbPushVibrations = (CheckBox) view.findViewById(R.id.cbPushVibrations);
        cbPushVibrations.setChecked(session.isNotificationVibrationEnabled());
        cbPushVibrations.setOnCheckedChangeListener(this);

        CheckBox cbUserVisibleNearby = (CheckBox) view.findViewById(R.id.cbUserVisibleNearby);
        cbUserVisibleNearby.setChecked(session.isVisibleNearby());
        cbUserVisibleNearby.setOnCheckedChangeListener(this);

        if(!getSession().isNotificationEnabled()) {
            view.findViewById(R.id.holderNotificationVibration).setEnabled(false);
            view.findViewById(R.id.cbPushVibrations).setEnabled(false);
        }

        view.findViewById(R.id.txtPrivacyPolicy).setOnClickListener(this);
        view.findViewById(R.id.txtTermsOfService).setOnClickListener(this);
        view.findViewById(R.id.txtDeleteAccount).setOnClickListener(this);
        view.findViewById(R.id.txtContact).setOnClickListener(this);
        view.findViewById(R.id.txtResetPassword).setOnClickListener(this);
        view.findViewById(R.id.txtLogOut).setOnClickListener(this);

        mSimpleFacebook = SimpleFacebook.getInstance(getActivity());

        if(getSession().isLoggedInByFacebook()) {
            view.findViewById(R.id.txtResetPassword).setOnClickListener(null);
            view.findViewById(R.id.txtResetPassword).setVisibility(View.GONE);
            view.findViewById(R.id.dividerResetPassword).setVisibility(View.GONE);
        }
    }

    private void onPushEnablingChanged(final CheckBox checkBox, final boolean isEnable) {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            checkBox.setChecked(!isEnable);
            return;
        }

        showProgress(getString(R.string.uploading));

        final String accessToken = getSession().getAccessToken();

        HttpClientManager.getInstance().updateNotificationEnabling(accessToken, String.valueOf(isEnable), new HttpClientManager.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                View view = getView();
                if (view == null) {
                    return;
                }

                hideProgress();
                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    checkBox.setOnCheckedChangeListener(null);
                    checkBox.setChecked(!isEnable);
                    checkBox.setOnCheckedChangeListener(SettingsFragment.this);
                    return;
                }

                view.findViewById(R.id.holderNotificationVibration).setEnabled(isEnable);
                view.findViewById(R.id.cbPushVibrations).setEnabled(isEnable);
                getSession().setNotificationEnabled(isEnable);
            }
        });
    }

    private void onPushVibrationCheckedChanged(boolean isEnabled) {
        getSession().setNotificationVibrationEnabled(isEnabled);
    }

    private void onTxtPrivacyPolicyClicked() {
        Fragment fragment = WebViewFragment.newInstance(SettingsFragment.this, getString(R.string.privacy_policy),
                URL_PRIVACY_POLICY);
        openFragment(fragment, WebViewFragment.class);
    }

    private void onVisibleNearbyCheckedChanged(final CheckBox checkBox, final boolean isVisible) {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            checkBox.setChecked(!isVisible);
            return;
        }

        showProgress(getString(R.string.uploading));

        final String accessToken = getSession().getAccessToken();

        HttpClientManager.getInstance().updateVisibleNearby(accessToken, String.valueOf(isVisible), new HttpClientManager.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                View view = getView();
                if (view == null) {
                    return;
                }

                hideProgress();
                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    checkBox.setOnCheckedChangeListener(null);
                    checkBox.setChecked(!isVisible);
                    checkBox.setOnCheckedChangeListener(SettingsFragment.this);
                    return;
                }

                getSession().setVisibleNearby(isVisible);
            }
        });
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

    private void onTxtResetPasswordClicked() {
        Intent intent = new Intent(getActivity(), ResetPasswordActivity.class);
        startActivity(intent);
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
        HttpClientManager.getInstance().deleteProfile(getActivity(), accessToken, new HttpClientManager.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (!failed) {
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
        Activity activity = getActivity();
        if(activity instanceof OnLogOutListener) {
            mSimpleFacebook.logout(mOnLogoutListener);

            OnLogOutListener listener = (OnLogOutListener) activity;
            listener.onLogOut();
        } else {
            throw new IllegalArgumentException("Your activity must implement OnLogOutListener!");
        }
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

    public interface OnLogOutListener {
        public void onLogOut();
    }
}
