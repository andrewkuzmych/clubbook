package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.ProfileAdapter;
import com.nl.clubbook.adapter.UserAvatarPagerAdapter;
import com.nl.clubbook.datasource.CheckInUserDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.FriendDto;
import com.nl.clubbook.datasource.UserPhotoDto;
import com.nl.clubbook.fragment.dialog.MessageDialog;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.ui.view.HorizontalListView;
import com.nl.clubbook.ui.view.ViewPagerBulletIndicatorView;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;
import com.nl.clubbook.utils.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends BaseInnerFragment implements View.OnClickListener, ViewPager.OnPageChangeListener,
        AdapterView.OnItemClickListener, MessageDialog.MessageDialogListener {

    private static final String ARG_OPEN_FRAGMENT_MODE = "ARG_OPEN_FRAGMENT_MODE";

    public static final int OPEN_FROM_CHAT = 4000;
    public static final int OPEN_MODE_DEFAULT = 6000;

    private static final String ARG_PROFILE_ID = "ARG_PROFILE_ID";

    private final int MODE_ADD = 33;
    private final int MODE_ACCEPT = 55;

    private String mProfileId;
    private String mUsername;
    private String mUserAvatarUrl;
    private List<CheckInUserDto> mCheckInUsers;
    private UserAvatarPagerAdapter mPhotoAdapter;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    private ViewPagerBulletIndicatorView mBulletIndicator;
    private int mBtnAddFriendMode = MODE_ADD;
    private int mOpenMode = OPEN_MODE_DEFAULT;

    public static Fragment newInstance(Fragment targetFragment, String profileId, List<CheckInUserDto> checkedInUsers, int openMode) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setTargetFragment(targetFragment, 0);
        fragment.setCheckInUsers(checkedInUsers);

        Bundle args = new Bundle();
        args.putString(ARG_PROFILE_ID, profileId);
        args.putInt(ARG_OPEN_FRAGMENT_MODE, openMode);
        fragment.setArguments(args);

        return fragment;
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_profile, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendScreenStatistic(R.string.user_screen_android);

        UIUtils.displayEmptyIconInActionBar((ActionBarActivity)getActivity());
        initActionBarTitle(getString(R.string.user_profile));
        handleExtras();
        initLoader();
        initView();
        initCheckInUserList();
        loadData(mProfileId);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            initActionBarTitle(getString(R.string.user_profile));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtAddFriend:
                onBtnAddFriendsClicked();
                break;
            case R.id.btnChat:
                onBtnChatClicked();
                break;
            case R.id.txtBlockUser:
                onBlockUserClicked();
                break;
            case R.id.txtRemoveFriend:
                onRemoveFriendClicked();
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }


    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onPageSelected(int position) {
        mBulletIndicator.setSelectedView(position);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        View viewUserId = view.findViewById(R.id.userId);
        String userId = (String)viewUserId.getTag();

        if(mProfileId != null && mProfileId.equalsIgnoreCase(userId)) {
            return;
        }

        mProfileId = userId;
        loadData(mProfileId);
    }

    @Override
    public void onPositiveButtonClick(MessageDialog dialogFragment) {
        doRemoveFriend();
    }

    @Override
    public void onNegativeButtonClick(MessageDialog dialogFragment) {
        dialogFragment.dismissAllowingStateLoss();
    }

    private void initLoader() {
        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_avatar_missing)
                .showImageOnFail(R.drawable.ic_avatar_unknown)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
    }

    private void handleExtras() {
        Bundle args = getArguments();
        if(args == null) {
            return;
        }

        mProfileId = args.getString(ARG_PROFILE_ID);
        mOpenMode = args.getInt(ARG_OPEN_FRAGMENT_MODE);
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        View btnChat = view.findViewById(R.id.btnChat);
        if(mProfileId != null && mProfileId.equalsIgnoreCase(getSession().getUserDetails().get(SessionManager.KEY_ID))) {
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

        if(mCheckInUsers == null) {
            view.findViewById(R.id.listCheckInUsers).setVisibility(View.GONE);
            return;
        }

        HorizontalListView listCheckInUser = (HorizontalListView) view.findViewById(R.id.listCheckInUsers);
        ProfileAdapter adapter = new ProfileAdapter(getActivity(), mCheckInUsers, ProfileAdapter.MODE_LIST);
        listCheckInUser.setAdapter(adapter);
        listCheckInUser.setVisibility(View.VISIBLE);
        listCheckInUser.setOnItemClickListener(this);
    }

    protected void loadData(@Nullable String profileId) {
        if(profileId == null) {
            L.i("Profile id = null");
            return;
        }

        if(!NetworkUtils.isOn(getActivity())) {
            getView().findViewById(R.id.scrollView).setVisibility(View.GONE);
            showToast(R.string.no_connection);
            return;
        }

        setRefreshing(getView(), true);

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();
        final String accessToken = user.get(SessionManager.KEY_ACCESS_TOCKEN);

        DataStore.retrieveUserFriend(accessToken, profileId, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                View view = getView();
                if (view == null || isDetached()) {
                    L.v("view == null || isDetached");
                    return;
                }

                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    return;
                }

                setRefreshing(view, false);
                fillProfile((FriendDto) result);
            }
        });
    }

    private void fillProfile(@Nullable FriendDto profile) {
        if(profile == null) {
            return;
        }

        View view = getView();
        if(view == null) {
            return;
        }

        List<UserPhotoDto> userPhotos = profile.getPhotos();
        if(userPhotos != null && !userPhotos.isEmpty()) {
            initViewPager(view, userPhotos);
        }

        // set name
        mUsername = profile.getName();
        TextView txtUserName = (TextView) view.findViewById(R.id.txtUsername);
        txtUserName.setText(mUsername);

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
        TextView txtAddFriends = (TextView) view.findViewById(R.id.txtAddFriend);
        String friendStatus = profile.getFriendStatus();
        if(FriendDto.STATUS_FRIEND.equalsIgnoreCase(friendStatus)) {
            txtAddFriends.setVisibility(View.GONE);
            view.findViewById(R.id.txtRemoveFriend).setVisibility(View.VISIBLE);
            mBtnAddFriendMode = -1;
        } else if(FriendDto.STATUS_RECEIVE_REQUEST.equalsIgnoreCase(friendStatus)) {
            txtAddFriends.setText(getString(R.string.accept_request));
            mBtnAddFriendMode = MODE_ACCEPT;
        } else if(FriendDto.STATUS_SENT_REQUEST.equalsIgnoreCase(friendStatus)) {
            initRequestPendingView(txtAddFriends);
            view.findViewById(R.id.txtRemoveFriend).setVisibility(View.GONE);
        } else {
            mBtnAddFriendMode = MODE_ADD;
        }

        final HashMap<String, String> user = getSession().getUserDetails();
        final String currentUserId = user.get(SessionManager.KEY_ID);

        if(profile.getId() != null && profile.getId().equals(currentUserId)) {
            view.findViewById(R.id.holderBlockRemoveUser).setVisibility(View.GONE);
            view.findViewById(R.id.txtAddFriend).setVisibility(View.GONE);
        }
    }

    private void initViewPager(@NotNull View view, @NotNull List<UserPhotoDto> userPhotoList) {
        mBulletIndicator = (ViewPagerBulletIndicatorView)view.findViewById(R.id.indicatorAvatars);
        mBulletIndicator.setBulletViewCount(userPhotoList.size());
        if(userPhotoList.size() <= 1) {
            mBulletIndicator.setVisibility(View.INVISIBLE);
        }

        mUserAvatarUrl = ImageHelper.getUserListAvatar(userPhotoList.get(0).getUrl());

        ViewPager pagerImage = (ViewPager) view.findViewById(R.id.pagerAvatars);
        if(mPhotoAdapter == null) {
            mPhotoAdapter = new UserAvatarPagerAdapter(getChildFragmentManager(), userPhotoList, mImageLoader, mOptions, animateFirstListener);
            pagerImage.setAdapter(mPhotoAdapter);
        } else {
            mPhotoAdapter.updateData(userPhotoList);
        }

        for(int i = 0; i < userPhotoList.size(); i++) {
            UserPhotoDto userPhoto = userPhotoList.get(i);
            if(userPhoto.getIsAvatar()) {
                pagerImage.setCurrentItem(i, false);
                mBulletIndicator.setSelectedView(i);

                mUserAvatarUrl = ImageHelper.getUserListAvatar(userPhoto.getUrl());
            }
        }

        pagerImage.setOnPageChangeListener(this);

        UIUtils.loadPhotoToActionBar((ActionBarActivity)getActivity(), mUserAvatarUrl);
    }

    private void setRefreshing(View view, boolean isRefreshing) {
        if(isRefreshing) {
            view.findViewById(R.id.scrollView).setVisibility(View.GONE);
            view.findViewById(R.id.listCheckInUsers).setVisibility(View.GONE);
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);

            if(mCheckInUsers != null && !mCheckInUsers.isEmpty()) {
                view.findViewById(R.id.listCheckInUsers).setVisibility(View.VISIBLE);
            }
        }
    }

    private void onBtnAddFriendsClicked() {
        if(mBtnAddFriendMode == MODE_ADD) {
            onAddFriendsClicked();
        } else {
            onAcceptFriendsRequestClicked();
        }
    }

    private void onAddFriendsClicked() {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        showProgress(getString(R.string.loading));

        DataStore.addFriendRequest(user.get(SessionManager.KEY_ID), mProfileId, user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new DataStore.OnResultReady() {

            @Override
            public void onReady(Object result, boolean failed) {
                View view = getView();
                if (view == null || isDetached()) {
                    return;
                }

                hideProgress();
                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                } else {
                    TextView txtView = (TextView) view.findViewById(R.id.txtAddFriend);
                    initRequestPendingView(txtView);
                }
            }
        });
    }

    private void onAcceptFriendsRequestClicked() {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        showProgress(getString(R.string.loading));

        DataStore.acceptFriendRequest(user.get(SessionManager.KEY_ID), mProfileId, user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new DataStore.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        View view = getView();
                        if (view == null || isDetached()) {
                            return;
                        }

                        hideProgress();
                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                        } else {
                            view.findViewById(R.id.txtAddFriend).setVisibility(View.GONE);
                            view.findViewById(R.id.txtRemoveFriend).setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void onBtnChatClicked() {
        if(mOpenMode == OPEN_FROM_CHAT) {
            closeFragment();
        } else {
            Fragment chatFragment = ChatFragment.newInstance(ProfileFragment.this, mProfileId, mUsername, mUserAvatarUrl);
            openFragment(chatFragment, ChatFragment.class);
        }
    }

    private void onBlockUserClicked() {
        //TODO
    }

    private void onRemoveFriendClicked() {
        showMessageDialog(
                ProfileFragment.this,
                getString(R.string.remove_friend),
                getString(R.string.are_you_sure_you_want_unfriend_this_user),
                getString(R.string.remove),
                getString(R.string.cancel)
        );
    }

    private void doRemoveFriend() {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        showProgress(getString(R.string.removing_friend));

        String userId = user.get(SessionManager.KEY_ID);
        String accessToken = user.get(SessionManager.KEY_ACCESS_TOCKEN);

        DataStore.unfriendRequest(accessToken, userId, mProfileId, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                View view = getView();
                if(view == null || isDetached()) {
                    return;
                }

                hideProgress();

                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                } else {
                    TextView txtAddFriend = (TextView) view.findViewById(R.id.txtAddFriend);
                    txtAddFriend.setVisibility(View.VISIBLE);
                    txtAddFriend.setText(getString(R.string.add_friend));

                    view.findViewById(R.id.txtRemoveFriend).setVisibility(View.GONE);
                }
            }
        });
    }

    private void initRequestPendingView(TextView txtAddFriend) {
        int leftPadding = txtAddFriend.getPaddingLeft();
        int rightPadding = txtAddFriend.getPaddingRight();

        txtAddFriend.setBackgroundResource(R.drawable.bg_btn_grey);
        txtAddFriend.setText(getString(R.string.request_pending));
        txtAddFriend.setOnClickListener(null);
        txtAddFriend.setPadding(leftPadding, 0, rightPadding, 0);
    }

    private void closeFragment() {
        FragmentTransaction fTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fTransaction.show(getTargetFragment());
        fTransaction.remove(ProfileFragment.this);
        fTransaction.commitAllowingStateLoss();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void setCheckInUsers(List<CheckInUserDto> mCheckInUsers) {
        this.mCheckInUsers = mCheckInUsers;
    }
}
