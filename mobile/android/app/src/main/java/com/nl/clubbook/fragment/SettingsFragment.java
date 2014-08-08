package com.nl.clubbook.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.activity.MainLoginActivity;

public class SettingsFragment extends BaseFragment {

    public SettingsFragment()
    {

    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        final BaseFragment thisInstance = this;
        Button editProfileButton = (Button) rootView.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                openFragment(new EditProfileFragment());
            }
        });


        Button logoutButton = (Button) rootView.findViewById(R.id.logoutButton);
        final MainActivity mainActivity = (MainActivity) getActivity();
        // open chat window
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                mainActivity.logout();
            }
        });
         
        return rootView;
    }


}
