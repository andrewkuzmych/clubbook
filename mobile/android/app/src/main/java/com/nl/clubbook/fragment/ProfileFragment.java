package com.nl.clubbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.ImagesGalleryActivity;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.User;
import com.nl.clubbook.datasource.UserPhoto;
import com.nl.clubbook.fragment.dialog.MessageDialog;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.NetworkUtils;
import com.nl.clubbook.utils.UIUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends BaseInnerFragment implements View.OnClickListener,
        MessageDialog.MessageDialogListener {

    private static final String ARG_OPEN_FRAGMENT_MODE = "ARG_OPEN_FRAGMENT_MODE";

    public static final int OPEN_FROM_CHAT = 4000;
    public static final int OPEN_MODE_DEFAULT = 6000;

    private User mUser;
    private String mUsername;

    private boolean mIsBlocked = false;

    private int mOpenMode = OPEN_MODE_DEFAULT;
    private int mBtnAddFriendMode = BtnAddFriendModes.MODE_ADD;
    private int mBtnBlockUserMode = BtnBlockModes.MODE_BLOCK;

    public static Fragment newInstance(@Nullable Fragment targetFragment, User currentUser, int openMode) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setTargetFragment(targetFragment, 0);
        fragment.setCurrentUser(currentUser);

        Bundle args = new Bundle();
        args.putInt(ARG_OPEN_FRAGMENT_MODE, openMode);
        fragment.setArguments(args);

        return fragment;
    }

    public static Fragment newInstance(User currentUser, int openMode) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setCurrentUser(currentUser);

        Bundle args = new Bundle();
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

        initTarget();

        UIUtils.displayEmptyIconInActionBar((ActionBarActivity) getActivity());
        initActionBarTitle(getString(R.string.user_profile));
        handleExtras();
        initView();
        fillProfile();
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
            case R.id.imgAvatar:
                onAvatarClicked();
                break;
        }
    }

    @Override
    public void onPositiveButtonClick(MessageDialog dialogFragment) {
        doRemoveFriend();
    }

    @Override
    public void onNegativeButtonClick(MessageDialog dialogFragment) {
        dialogFragment.dismissAllowingStateLoss();
    }

    private void handleExtras() {
        Bundle args = getArguments();
        if(args == null) {
            return;
        }

        mOpenMode = args.getInt(ARG_OPEN_FRAGMENT_MODE);
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        View btnChat = view.findViewById(R.id.btnChat);
        View btnBlockUser = view.findViewById(R.id.txtBlockUser);
        if(mUser != null && mUser.getId() != null && mUser.getId().equalsIgnoreCase(getSession().getUserDetails().get(SessionManager.KEY_ID))) {
            btnChat.setVisibility(View.GONE);
            btnBlockUser.setVisibility(View.GONE);
        } else {
            btnChat.setOnClickListener(this);
            btnBlockUser.setOnClickListener(this);
        }

        view.findViewById(R.id.txtAddFriend).setOnClickListener(this);
        view.findViewById(R.id.txtRemoveFriend).setOnClickListener(this);
        view.findViewById(R.id.imgAvatar).setOnClickListener(this);
    }

    private void fillProfile() {
        View view = getView();
        if(view == null || mUser == null) {
            return;
        }

        TextView txtBlockUser = (TextView) view.findViewById(R.id.txtBlockUser);
        if(mUser.isBlocked()) {
            mIsBlocked = true;
            mBtnBlockUserMode = BtnBlockModes.MODE_UNBLOCK;
            txtBlockUser.setText(R.string.unblock_user);
        } else {
            mIsBlocked = false;
            mBtnBlockUserMode = BtnBlockModes.MODE_BLOCK;
            txtBlockUser.setText(R.string.block_user);
        }

        ImageView imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
        String avatarUrl = mUser.getAvatar();
        if(!TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(getActivity()).load(avatarUrl).error(R.drawable.ic_avatar_unknown).into(imgAvatar);
            UIUtils.loadPhotoToActionBar((ActionBarActivity) getActivity(), ImageHelper.getUserListAvatar(avatarUrl), mTarget);
        } else {
            imgAvatar.setImageResource(R.drawable.ic_avatar_missing);
        }

        // set name
        mUsername = mUser.getName();
        TextView txtUserName = (TextView) view.findViewById(R.id.txtUsername);
        txtUserName.setText(mUsername);

        //set user info
        TextView txtUserInfo = (TextView) view.findViewById(R.id.txtUserInfo);
        String age = mUser.getAge();
        String ageToDisplay = (age != null && age.length() > 0) ? mUser.getAge() + ", " : "";
        String gender = mUser.getGender() != null ? mUser.getGender() : "";
        txtUserInfo.setText(ageToDisplay + gender);

        //set country
        TextView txtCountry = (TextView) view.findViewById(R.id.txtCountry);
        String country = mUser.getCountry();
        if(!TextUtils.isEmpty(country)) {
            txtCountry.setText(country);
        } else {
            txtCountry.setVisibility(View.GONE);
        }

        //check is this user your friend
        TextView txtAddFriends = (TextView) view.findViewById(R.id.txtAddFriend);
        String friendStatus = mUser.getFriendStatus();
        if(User.STATUS_FRIEND.equalsIgnoreCase(friendStatus)) {
            txtAddFriends.setVisibility(View.GONE);
            view.findViewById(R.id.txtRemoveFriend).setVisibility(View.VISIBLE);
            mBtnAddFriendMode = -1;
        } else if(User.STATUS_RECEIVE_REQUEST.equalsIgnoreCase(friendStatus)) {
            txtAddFriends.setText(getString(R.string.accept_request));
            mBtnAddFriendMode = BtnAddFriendModes.MODE_ACCEPT;
        } else if(User.STATUS_SENT_REQUEST.equalsIgnoreCase(friendStatus)) {
            mBtnAddFriendMode = BtnAddFriendModes.MODE_CANCEL;
            txtAddFriends.setText(R.string.cancel_request);
            view.findViewById(R.id.txtRemoveFriend).setVisibility(View.GONE);
        } else {
            mBtnAddFriendMode = BtnAddFriendModes.MODE_ADD;
        }

        String currentUserId = getSession().getUserId();
        if(currentUserId.equalsIgnoreCase(mUser.getId())) {
            view.findViewById(R.id.holderButtons).setVisibility(View.GONE);
            view.findViewById(R.id.txtAddFriend).setVisibility(View.GONE);
        }
    }

    private void onBtnAddFriendsClicked() {
        if(mIsBlocked) {
            showMessageDialog(getString(R.string.app_name), getString(R.string.you_cannot_add_remove_friend_when_user_is_blocked));
            return;
        }

        if(mBtnAddFriendMode == BtnAddFriendModes.MODE_ADD) {
            onAddFriendsClicked();
        } else if(mBtnAddFriendMode == BtnAddFriendModes.MODE_CANCEL){
            onCancelFriendRequestClicked();
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

        DataStore.addFriendRequest(user.get(SessionManager.KEY_ID), mUser.getId(), user.get(SessionManager.KEY_ACCESS_TOCKEN),
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
                    mBtnAddFriendMode = BtnAddFriendModes.MODE_CANCEL;
                    TextView txtAddFriends = (TextView) view.findViewById(R.id.txtAddFriend);
                    txtAddFriends.setText(R.string.cancel_request);
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

        DataStore.acceptFriendRequest(user.get(SessionManager.KEY_ID), mUser.getId(), user.get(SessionManager.KEY_ACCESS_TOCKEN),
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

    private void onCancelFriendRequestClicked() {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        showProgress(getString(R.string.canceling));

        DataStore.cancelFriendRequest(user.get(SessionManager.KEY_ID), mUser.getId(), user.get(SessionManager.KEY_ACCESS_TOCKEN),
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
                            mBtnAddFriendMode = BtnAddFriendModes.MODE_ADD;
                            TextView txtAddFriend = (TextView) view.findViewById(R.id.txtAddFriend);
                            txtAddFriend.setText(R.string.add_friend);
                        }
                    }
                });
    }

    private void onBtnChatClicked() {
        if(mIsBlocked) {
            showMessageDialog(getString(R.string.app_name), getString(R.string.you_cannot_add_remove_friend_when_user_is_blocked));
            return;
        }

        if(mOpenMode == OPEN_FROM_CHAT) {
            closeFragment();
        } else {
            Fragment chatFragment = ChatFragment.newInstance(
                    ProfileFragment.this,
                    ChatFragment.MODE_OPEN_FROM_PROFILE, mUser.getId(),
                    mUsername,
                    ImageHelper.getUserListAvatar(mUser.getAvatar())
            );
            openFragment(chatFragment, ChatFragment.class);
        }
    }

    private void onBlockUserClicked() {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        if(mBtnBlockUserMode == BtnBlockModes.MODE_BLOCK) {
            blockUser();
        } else {
            unblockUser();
        }
    }

    private void blockUser() {
        SessionManager sessionManager = getSession();

        showProgress(getString(R.string.block_user_process));
        DataStore.blockUserRequest(sessionManager.getUserId(), mUser.getId(), sessionManager.getAccessToken(),
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
                            mIsBlocked = true;
                            mBtnBlockUserMode = BtnBlockModes.MODE_UNBLOCK;
                            TextView txtBlockUser = (TextView)view.findViewById(R.id.txtBlockUser);
                            txtBlockUser.setText(R.string.unblock_user);
                        }
                    }
                });
    }

    private void unblockUser() {
        SessionManager sessionManager = getSession();

        showProgress(getString(R.string.unblock_user_process));
        DataStore.unblockUserRequest(sessionManager.getUserId(), mUser.getId(), sessionManager.getAccessToken(),
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
                            mIsBlocked = false;
                            mBtnBlockUserMode = BtnBlockModes.MODE_BLOCK;
                            TextView txtBlockUser = (TextView)view.findViewById(R.id.txtBlockUser);
                            txtBlockUser.setText(R.string.block_user);
                        }
                    }
                });
    }


    private void onRemoveFriendClicked() {
        if(mIsBlocked) {
            showMessageDialog(getString(R.string.app_name), getString(R.string.you_cannot_add_remove_friend_when_user_is_blocked));
            return;
        }

        showMessageDialog(
                ProfileFragment.this,
                getString(R.string.remove_friend),
                getString(R.string.are_you_sure_you_want_unfriend_this_user),
                getString(R.string.remove),
                getString(R.string.cancel)
        );
    }

    private void onAvatarClicked() {
        List<UserPhoto> photos = mUser.getPhotos();
        if(photos == null || photos.isEmpty()) {
            return;
        }

        String[] urls = new String[photos.size()];
        for(int i = 0; i < photos.size(); i++) {
            UserPhoto photo = photos.get(i);
            urls[i] = photo.getUrl();
        }

        Intent intent = new Intent(getActivity(), ImagesGalleryActivity.class);
        intent.putExtra(ImagesGalleryActivity.EXTRA_PHOTOS_URLS, urls);
        intent.putExtra(ImagesGalleryActivity.EXTRA_SELECTED_PHOTO, 0);
        startActivity(intent);
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

        DataStore.unfriendRequest(accessToken, userId, mUser.getId(), new DataStore.OnResultReady() {
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
                    mBtnAddFriendMode = BtnAddFriendModes.MODE_ADD;
                    TextView txtAddFriend = (TextView) view.findViewById(R.id.txtAddFriend);
                    txtAddFriend.setVisibility(View.VISIBLE);
                    txtAddFriend.setText(getString(R.string.add_friend));

                    view.findViewById(R.id.txtRemoveFriend).setVisibility(View.GONE);
                }
            }
        });
    }

    public void setCurrentUser(User currentUser) {
        mUser = currentUser;
    }

    private interface BtnBlockModes {
        final int MODE_BLOCK = 874;
        final int MODE_UNBLOCK = 478;
    }

    private interface BtnAddFriendModes {
        final int MODE_ADD = 33;
        final int MODE_ACCEPT = 55;
        final int MODE_CANCEL = 77;
    }
}
