package com.nl.clubbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.FindFriendsAdapter;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnFriendsListener;
import com.sromku.simple.fb.listeners.OnInviteListener;
import com.sromku.simple.fb.listeners.OnLoginListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodymyr on 07.10.2014.
 */
public class FindFriendsFragment extends BaseFragment implements View.OnClickListener {

    private SimpleFacebook mSimpleFacebook;

    private final int LOGIN_FOR_RETRIEVE_FRIENDS = 100;
    private final int LOGIN_FOR_INVITE_FRIENDS = 200;

    private int mLoginMode = LOGIN_FOR_RETRIEVE_FRIENDS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_find_friends, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mSimpleFacebook.onActivityResult(getActivity(), requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtInviteFriends:
                onInviteFriendsClicked();
                break;
            case R.id.txtConnectFacebook:
                onConnectFacebookClicked();
                break;
        }
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mSimpleFacebook = SimpleFacebook.getInstance(getActivity());

        view.findViewById(R.id.txtInviteFriends).setOnClickListener(this);
        view.findViewById(R.id.txtConnectFacebook).setOnClickListener(this);
    }

    private void onInviteFriendsClicked() {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        if(mSimpleFacebook.isLogin()) {
            sendFacebookRequest();
        } else {
            mLoginMode = LOGIN_FOR_INVITE_FRIENDS;
            mSimpleFacebook.login(mOnLoginListener);
        }
    }

    private void onConnectFacebookClicked() {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        if(mSimpleFacebook.isLogin()) {
            loadFriends();
        } else {
            mLoginMode = LOGIN_FOR_RETRIEVE_FRIENDS;
            mSimpleFacebook.login(mOnLoginListener);
        }
    }

    private void loadFriends() {
        mSimpleFacebook.getFriends(new OnFriendsListener() {
            @Override
            public void onComplete(List<Profile> response) {
                List<String> ids = new ArrayList<String>();
                for(Profile profile : response) {
                    ids.add(profile.getId());
                }

                doLoadUserFriendsOnClubbook(ids);
            }

            @Override
            public void onThinking() {
                super.onThinking();
            }

            @Override
            public void onFail(String reason) {
                super.onFail(reason);
            }

            @Override
            public void onException(Throwable throwable) {
                super.onException(throwable);
            }
        });
    }

    private void doLoadUserFriendsOnClubbook(@NotNull List<String> ids) {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        if(ids.isEmpty()) {
            return;
        }

        getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        SessionManager sessionManager = getSession();
        String accessToken = sessionManager.getAccessToken();

        DataStore.getFacebookFriendsOnClubbook(accessToken, ids, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                View view = getView();
                if(isDetached() || view == null) {
                    return;
                }

                view.findViewById(R.id.progressBar).setVisibility(View.GONE);

                if(result == null) {
                    return;
                }

                List<UserDto> users = (List<UserDto>) result;
                if(users.isEmpty()) {
                    //TODO
                    return;
                }

                view.findViewById(R.id.txtConnectFacebook).setEnabled(false);

                FindFriendsAdapter adapter = new FindFriendsAdapter(getActivity(), users);
                ListView listAvailableFriends = (ListView) view.findViewById(R.id.listAvailableFriends);
                listAvailableFriends.setAdapter(adapter);
            }
        });
    }

    private void sendFacebookRequest() {
        mSimpleFacebook.invite("Test message", new OnInviteListener() {
                    @Override
                    public void onComplete(List<String> strings, String s) {
                        showToast(R.string.request_sent);
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        L.d("exception - " + throwable.getCause());
                        showToast(R.string.something_went_wrong_please_try_again);
                    }

                    @Override
                    public void onFail(String s) {
                        L.d("fail - " + s);
                        showToast(R.string.something_went_wrong_please_try_again);
                    }
                }, "");

    }

    private OnLoginListener mOnLoginListener = new OnLoginListener() {

        @Override
        public void onFail(String reason) {
            showToast(R.string.something_went_wrong_please_try_again);
        }

        @Override
        public void onException(Throwable throwable) {
            showToast(R.string.something_went_wrong_please_try_again);
        }

        @Override
        public void onThinking() {
        }

        @Override
        public void onLogin() {
            if(mLoginMode == LOGIN_FOR_INVITE_FRIENDS) {
                sendFacebookRequest();
            } else {
                loadFriends();
            }
        }

        @Override
        public void onNotAcceptingPermissions(Permission.Type type) {
        }
    };
}
