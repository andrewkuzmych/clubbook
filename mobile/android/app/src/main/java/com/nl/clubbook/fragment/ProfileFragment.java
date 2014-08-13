package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.adapter.ProfileAdapter;
import com.nl.clubbook.adapter.UserAvatarPagerAdapter;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.FriendDto;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.datasource.UserPhotoDto;
import com.nl.clubbook.helper.*;
import com.nl.clubbook.ui.view.HorizontalListView;
import com.nl.clubbook.utils.L;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends BaseFragment implements View.OnClickListener, AbsListView.OnScrollListener {
    private String mFriendProfileId;
    private List<UserDto> mCheckInUsers;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    public ProfileFragment()
    {

    }

    public ProfileFragment(BaseFragment previousFragment, String profileId, List<UserDto> checkInedUsers) {
        super(previousFragment);
        this.mFriendProfileId = profileId;
        this.mCheckInUsers = checkInedUsers;
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
        initCheckInUserList();
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
                openFragment(new ChatFragment(ProfileFragment.this, mFriendProfileId, "Jon"));
                break;
            case R.id.txtBlockUser:
                onBlockUserClicked();
                break;
            case R.id.txtRemoveFriend:
                onRemoveFriendClicked();
                break;
        }
    }

    private void initLoader() {
        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_list_image)
                .showImageOnFail(R.drawable.default_list_image)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
        //.showStubImage(R.drawable.default_list_image)
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
        if(mFriendProfileId != null && mFriendProfileId.equalsIgnoreCase(this.getSession().getUserDetails().get(SessionManager.KEY_ID))){
            btnChat.setVisibility(View.GONE);
        } else {
            btnChat.setOnClickListener(this);
        }

        view.findViewById(R.id.txtBlockUser).setOnClickListener(this);
        view.findViewById(R.id.txtAddFriend).setOnClickListener(this);
        view.findViewById(R.id.txtRemoveFriend).setOnClickListener(this);
    }

    private void initCheckInUserList() {
        View view = getView();
        if(view == null) {
            return;
        }

        if(mCheckInUsers == null || true) {
            L.e("!visible");
//            view.findViewById(R.id.listCheckInUsers).setVisibility(View.GONE); //TODO
            View scrollView = view.findViewById(R.id.scrollView);
            scrollView.invalidate();

//            scrollView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
//                    RelativeLayout.LayoutParams.FILL_PARENT));
            return;
        }


        //TODO test
        UserDto userDto = mCheckInUsers.get(0);
        for(int i = 0; i < 100; i++) {
            mCheckInUsers.add(userDto);
        }
        //

        L.e("visible");
//        HorizontalListView listCheckInUser = (HorizontalListView) view.findViewById(R.id.listCheckInUsers);
//        ProfileAdapter adapter = new ProfileAdapter(getActivity(), mCheckInUsers);
//        listCheckInUser.setAdapter(adapter);
//        listCheckInUser.setVisibility(View.VISIBLE);
    }

    protected void loadData() {
        setRefreshing(getView(), true);

        final SessionManager session = new SessionManager(getActivity());
        final HashMap<String, String> user = session.getUserDetails();

        DataStore.retrieveUserFriend(user.get(SessionManager.KEY_ID), mFriendProfileId, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (getView() == null || isDetached()) {
                    L.v("view == null || isDetached");
                    return;
                }

                setRefreshing(getView(), false);
                if (failed) {
                    showNoInternetActivity();
                    return;
                }

                fillProfile((FriendDto) result);
            }
        });
    }

    private void fillProfile(FriendDto profile) {
        if(profile == null) {
            L.v("profile = null");
            return;
        }

        View view = getView();
        if(view == null) {
            L.v("view = null");
            return;
        }

        List<UserPhotoDto> userPhotos = profile.getPhotos();
        if(userPhotos != null) {
            initViewPager(view, userPhotos);
        }

        // set name
        TextView txtUserName = (TextView) view.findViewById(R.id.txtUsername);
        txtUserName.setText(profile.getName());

        // set avatar //TODO

        //set user info
        TextView txtUserInfo = (TextView) view.findViewById(R.id.txtUserInfo);
        String age = profile.getAge();
        String ageToDisplay = (age != null && age.length() > 0) ? profile.getAge() + ", " : "";
        String gender = profile.getGender() != null ? profile.getGender() : "";
        txtUserInfo.setText(ageToDisplay + gender);

        //set country
        TextView txtCountry = (TextView) view.findViewById(R.id.txtCountry);
        String country = profile.getCountry();
        if(!TextUtils.isEmpty(country)) {
            txtCountry.setText(country);
        } else {
            txtCountry.setVisibility(View.GONE);
            view.findViewById(R.id.dividerCoutnry).setVisibility(View.GONE);
        }

        //set about me
        String aboutMe = profile.getBio();
        if(!TextUtils.isEmpty(aboutMe)) {
            TextView txtAboutMe = (TextView) view.findViewById(R.id.txtAboutMe);
            txtAboutMe.setText(aboutMe);
        } else {
            view.findViewById(R.id.txtAboutMe).setVisibility(View.GONE);
            view.findViewById(R.id.txtAboutMeLabel).setVisibility(View.GONE);
            view.findViewById(R.id.dividerAboutMe).setVisibility(View.GONE);
        }

        //check is this user your friend
        String friendStatus = profile.getFriendStatus();
        L.v("friendStatus - " + friendStatus);
        if(FriendDto.STATUS_FRIEND.equalsIgnoreCase(friendStatus)) {
            view.findViewById(R.id.txtAddFriend).setVisibility(View.GONE);
            view.findViewById(R.id.txtRemoveFriend).setVisibility(View.VISIBLE);
        }
    }

    private void initViewPager(View view, List<UserPhotoDto> userPhotoList) {
        ViewPager pagerImage = (ViewPager) view.findViewById(R.id.pagerAvatars);
        UserAvatarPagerAdapter adapter = new UserAvatarPagerAdapter(getChildFragmentManager(), userPhotoList, mImageLoader, mOptions, animateFirstListener);
        pagerImage.setAdapter(adapter);

        for(int i = 0; i < userPhotoList.size(); i++) {
            UserPhotoDto userPhoto = userPhotoList.get(i);
            if(userPhoto.getIsAvatar()) {
                pagerImage.setCurrentItem(i, false);
            }
        }

        //TODO implement page change listener
    }

    private void setRefreshing(View view, boolean isRefreshing) {
        if(isRefreshing) {
            view.findViewById(R.id.scrollView).setVisibility(View.GONE);
//            view.findViewById(R.id.listCheckInUsers).setVisibility(View.GONE); //TODO
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
//            view.findViewById(R.id.listCheckInUsers).setVisibility(View.VISIBLE); //TODO
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }

    private void onAddFriendsClicked() {
        final SessionManager session = new SessionManager(getActivity());
        final HashMap<String, String> user = session.getUserDetails();

        ((BaseActivity) getActivity()).showProgress("Loading...");

        DataStore.addFriendRequest(user.get(SessionManager.KEY_ID), mFriendProfileId, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                View view = getView();
                if(view == null || isDetached()) {
                    return;
                }

                if (failed) {
                    ((BaseActivity) getActivity()).hideProgress(false);
                } else {
                    ((BaseActivity) getActivity()).hideProgress(true);

                    view.findViewById(R.id.txtAddFriend).setVisibility(View.GONE);
                    view.findViewById(R.id.txtRemoveFriend).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void onBlockUserClicked() {
        //TODO
    }

    private void onRemoveFriendClicked() {
        final SessionManager session = new SessionManager(getActivity());
        final HashMap<String, String> user = session.getUserDetails();

        ((BaseActivity) getActivity()).showProgress("Loading...");

        DataStore.removeFriendRequest(user.get(SessionManager.KEY_ID), mFriendProfileId, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                View view = getView();
                if(view == null || isDetached()) {
                    return;
                }

                if (failed) {
                    ((BaseActivity) getActivity()).hideProgress(true);
                } else {
                    ((BaseActivity) getActivity()).hideProgress(false);

                    view.findViewById(R.id.txtAddFriend).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.txtRemoveFriend).setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
