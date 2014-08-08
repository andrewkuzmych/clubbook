package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ProfileFragment extends BaseFragment {
    private TextView userName;
    private ImageView userAvatar;
    private Button chatButton;
    private String profile_id;
    private UserDto profile;

    protected ImageLoader imageLoader;
    protected DisplayImageOptions options;
    protected ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

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
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        chatButton = (Button) rootView.findViewById(R.id.chat_button);
        if(profile_id.equalsIgnoreCase(this.getSession().getUserDetails().get(SessionManager.KEY_ID))){
            chatButton.setVisibility(View.GONE);
        }
        userName = (TextView) rootView.findViewById(R.id.user_name);
        userAvatar = (ImageView) rootView.findViewById(R.id.avatar);

        getActivity().setTitle(getString(R.string.header_profile));

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_list_image)
                .showImageForEmptyUri(R.drawable.default_list_image)
                .showImageOnFail(R.drawable.default_list_image)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

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

                setHandlers();

                // update UI components
                // set name
                userName.setText(profile.getName());
                // set avatar
                String image_url = ImageHelper.getProfileImage(profile.getAvatar());
                imageLoader.displayImage(image_url, userAvatar, options, animateFirstListener);
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
