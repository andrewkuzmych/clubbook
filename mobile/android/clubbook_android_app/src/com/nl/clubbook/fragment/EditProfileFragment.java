package com.nl.clubbook.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.AlertDialogManager;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.HashMap;

public class EditProfileFragment extends BaseFragment {

    EditText user_text;
    private Button saveButton;
    private UserDto profile;
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        saveButton = (Button) rootView.findViewById(R.id.save_profile_button);
        user_text = (EditText) rootView.findViewById(R.id.name_text);

        getActivity().setTitle(getString(R.string.header_profile));

        loadData();

        return rootView;
    }

    private void setHandlers() {
        final BaseFragment thisInstance = this;

        // save user data
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                // validate
                String errorTitle = "Update profile failed.";

                String user_name = user_text.getText().toString().trim();
                if (user_name.trim().length() < 2) {
                    alert.showAlertDialog(getActivity(), errorTitle, getString(R.string.name_incorrect), false);
                    return;
                }

                // update on server side
                ((BaseActivity) getActivity()).showProgress("Loading...");

                DataStore.updateUserProfile(thisInstance.getSession().getUserDetails().get(SessionManager.KEY_ID),
                        user_name, "", "", new DataStore.OnResultReady() {
                            @Override
                            public void onReady(Object result, boolean failed) {
                                if (failed) {
                                    ((BaseActivity) getActivity()).hideProgress(false);
                                    return;
                                }

                                ((BaseActivity) getActivity()).hideProgress(true);

                                profile = (UserDto) result;

                                // update UI
                                ((MainActivity) getActivity()).updateMyInformation(profile);
                            }
                        }
                );
            }
        });
    }

    protected void loadData() {
        showProgress();

        DataStore.retrieveUser(this.getSession().getUserDetails().get(SessionManager.KEY_ID), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    hideProgress(false);
                    return;
                }

                hideProgress(true);

                profile = (UserDto) result;

                setHandlers();

                // update UI components
                user_text.setText(profile.getName());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (((MainActivity) getActivity()).getDrawerToggle().isDrawerIndicatorEnabled()) {
            ((MainActivity) getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(false);
            ((MainActivity) getActivity()).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void backButtonWasPressed() {
        ((MainActivity) getActivity()).setDefaultTitle();
        if (!((MainActivity) getActivity()).getDrawerToggle().isDrawerIndicatorEnabled()) {
            ((MainActivity) getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(true);
            ((MainActivity) getActivity()).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
}
