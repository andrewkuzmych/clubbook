package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.datasource.UserPhotoDto;
import com.nl.clubbook.helper.*;
import com.nl.clubbook.utils.L;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.List;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {
    private String mProfileId;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();
    private List<UserPhotoDto> mUserPhotos;

    public ProfileFragment()
    {

    }

    public ProfileFragment(BaseFragment previousFragment, String profileId) {
        super(previousFragment);
        this.mProfileId = profileId;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtAddFriend:
                onAddFriendsClicked();
                break;
            case R.id.btnChat:
                openFragment(new ChatFragment(ProfileFragment.this, mProfileId, "Jon"));
                break;
            case R.id.txtBlockUser:
                onBlockUserClicked();
                break;
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
        } else {
            btnChat.setOnClickListener(this);
        }

        view.findViewById(R.id.txtBlockUser).setOnClickListener(this);
        view.findViewById(R.id.txtAddFriend).setOnClickListener(this);

        final ImageView imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
        final ImageSwitcher swicherAvatar = (ImageSwitcher) view.findViewById(R.id.switcherAvatar);
        swicherAvatar.setOnTouchListener(new View.OnTouchListener() {
            float initialX = 0f;
            int position = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mUserPhotos == null || mUserPhotos.size() <= 1) {
                    return false;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        float finalX = event.getX();
                        if (initialX > finalX) {
                            swicherAvatar.setInAnimation(getActivity(), R.anim.left_in);
                            swicherAvatar.setOutAnimation(getActivity(), R.anim.left_out);

                            // next
                            position++;
                            if (position >= mUserPhotos.size()) {
                                position = 0;
                            }

                            String image_url = ImageHelper.getProfileImage(mUserPhotos.get(position).getUrl());
                            mImageLoader.displayImage(image_url, imgAvatar, mOptions, animateFirstListener);
//                            txtImageSlider.setText(String.valueOf(position + 1) + "/" + String.valueOf(mClub.getPhotos().size())); //TODO

                            swicherAvatar.showNext();

                        } else {
                            swicherAvatar.setInAnimation(getActivity(), R.anim.right_in);
                            swicherAvatar.setOutAnimation(getActivity(), R.anim.right_out);

                            // prev
                            if (position > 0) {
                                position = position - 1;
                            } else {
                                position = mUserPhotos.size() - 1;
                            }

                            String image_url = ImageHelper.getProfileImage(mUserPhotos.get(position).getUrl());
                            mImageLoader.displayImage(image_url, imgAvatar, mOptions, animateFirstListener);
//                            txtImageSlider.setText(String.valueOf(position + 1) + "/" + String.valueOf(mUserPhotos.size())); //TODO

                            swicherAvatar.showPrevious();
                        }
                        break;
                }
                return true;
            }
        });
    }

    protected void loadData() {
        setRefreshing(getView(), true);

        DataStore.retrieveUser(mProfileId, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if(getView() == null || isDetached()) {
                    return;
                }

                setRefreshing(getView(), false);
                if (failed) {
                    //TODO
                    return;
                }

                fillProfile((UserDto) result);
            }
        });
    }

    private void fillProfile(UserDto profile) {
        if(profile == null) {
            L.v("profile = null");
            return;
        }

        View view = getView();
        if(view == null) {
            L.v("view = null");
            return;
        }

        mUserPhotos = profile.getPhotos();

        // set name
        TextView txtUserName = (TextView) view.findViewById(R.id.txtUsername);
        txtUserName.setText(profile.getName());

        // set avatar
        ImageView imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
        String image_url = ImageHelper.getProfileImage(profile.getAvatar());
        mImageLoader.displayImage(image_url, imgAvatar, mOptions, animateFirstListener);

        //set user info
        TextView txtUserInfo = (TextView) view.findViewById(R.id.txtUserInfo);
        String age = profile.getAge();
        String ageToDisplay = (age != null && age.length() > 0) ? profile.getAge() + ", " : "";
        String gender = profile.getGender() != null ? profile.getGender() : "";
        txtUserInfo.setText(ageToDisplay + gender);

        //set country
        String country = profile.getCountry();
        TextView txtCountry = (TextView) view.findViewById(R.id.txtCountry);
        txtCountry.setText(country != null ? country : "");

        //set about me
        String aboutMe = profile.getBio();
        TextView txtAboutMe = (TextView) view.findViewById(R.id.txtAboutMe);
        txtAboutMe.setText(aboutMe != null ? aboutMe : "");
    }

    private void setRefreshing(View view, boolean isRefreshing) {
        if(isRefreshing) {
            view.findViewById(R.id.scrollView).setVisibility(View.GONE);
            view.findViewById(R.id.listCheckInUsers).setVisibility(View.GONE);
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.listCheckInUsers).setVisibility(View.VISIBLE);
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }

    private void onAddFriendsClicked() {
        //TODO
    }

    private void onBlockUserClicked() {
        //TODO
    }
}
