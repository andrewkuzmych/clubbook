package com.nl.clubbook.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.adapter.ProfileAdapter;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.*;

import java.util.HashMap;

public class ProfileFragment extends BaseFragment {
    private TextView userName;
    private Button chatButton;
    private String profile_id;
    private UserDto profile;

    public ProfileFragment()
    {

    }

    public ProfileFragment(BaseFragment previousFragment, String profile_id) {
        super(previousFragment);
        this.profile_id = profile_id;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getActivity().setTitle("Jon");
 
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        chatButton = (Button) rootView.findViewById(R.id.chat_button);
        userName = (TextView) rootView.findViewById(R.id.user_name);

        loadData();

        return rootView;
    }

    private void setHandlers() {
        final BaseFragment thisInstance = this;

        // open chat window
        chatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                openFragment(new ChatFragment(thisInstance, profile_id, "Jon"));
            }
        });
    }

    protected void loadData() {
        showProgress();

        DataStore.retrieveUser(profile_id, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    hideProgress(false);
                    return;
                }

                hideProgress(true);

                profile = (UserDto) result;

                getActivity().setTitle(profile.getName());
                setHandlers();

                // update UI components
                userName.setText(profile.getName());
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
        ((MainActivity) getActivity()).setDefoltTitle();
        if (!((MainActivity) getActivity()).getDrawerToggle().isDrawerIndicatorEnabled()) {
            ((MainActivity) getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(true);
            ((MainActivity) getActivity()).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
}
