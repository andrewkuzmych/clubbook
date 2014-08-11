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
    private String mProfileId;
    private UserDto profile;

    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mOptions;
    protected ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    public ProfileFragment()
    {

    }

    public ProfileFragment(BaseFragment previousFragment, String profile_id) {
        super(previousFragment);
        this.mProfileId = profile_id;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initLoader();
        initActionBar();
        initView();
        loadData();
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

    private void initLoader() {
        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_list_image)
                .showImageForEmptyUri(R.drawable.default_list_image)
                .showImageOnFail(R.drawable.default_list_image)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
    }

    private void initActionBar() {
        //TODO fix this code
        getActivity().setTitle(getString(R.string.header_profile));
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        View btnChat = view.findViewById(R.id.btnChat);
        if(mProfileId != null && mProfileId.equalsIgnoreCase(this.getSession().getUserDetails().get(SessionManager.KEY_ID))){
            btnChat.setVisibility(View.GONE);
        }

        btnChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                openFragment(new ChatFragment(ProfileFragment.this, mProfileId, "Jon"));
            }
        });
    }

    protected void loadData() {
        showProgress();

        DataStore.retrieveUser(mProfileId, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    hideProgress(false);
                    return;
                }

                hideProgress(true);

                profile = (UserDto) result;

                //TODO fill all views

                View view = getView();
                if(view == null) {
                    return;
                }

                // set name
                TextView txtUserName = (TextView) view.findViewById(R.id.txtUsername);
                txtUserName.setText(profile.getName());

                // set avatar
                ImageView imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
                String image_url = ImageHelper.getProfileImage(profile.getAvatar());
                mImageLoader.displayImage(image_url, imgAvatar, mOptions, animateFirstListener);
            }
        });
    }
}
