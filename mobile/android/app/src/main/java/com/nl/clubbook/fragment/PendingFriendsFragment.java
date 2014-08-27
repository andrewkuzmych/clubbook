package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.adapter.PendingFriendsAdapter;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Volodymyr on 26.08.2014.
 */
public class PendingFriendsFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private PendingFriendsAdapter mAdapter;

    public static Fragment newInstance(Fragment targetFragment) {
        Fragment fragment = new PendingFriendsFragment();
        fragment.setTargetFragment(targetFragment, 0);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_pending_friends, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        loadData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        //TODO update this
        if(!hidden) {
            refreshFriends();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String userId = view.findViewById(R.id.txtName).getTag().toString();
        Fragment fragment = ProfileFragment.newInstance(getTargetFragment(), userId, null);
        openFragment(fragment, ProfileFragment.class);
    }

    @Override
    public void onClick(View v) {
        String userId = (String)v.getTag();

        switch (v.getId()) {
            case R.id.imgAccept:
                onAcceptClicked(userId);
                break;
            case R.id.imgDecline:
                onDeclineClicked(userId);
                break;
        }
    }

    @Override
    protected void loadData() {
        doLoadPendingFriends();
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mAdapter = new PendingFriendsAdapter(getActivity(), new ArrayList<UserDto>(), this);
        ListView listPendingFriends = (ListView) view.findViewById(R.id.listPendingFriends);
        listPendingFriends.setAdapter(mAdapter);
        listPendingFriends.setOnItemClickListener(this);
    }

    private void doLoadPendingFriends() {
        final View view = getView();
        if(view == null || mSwipeRefreshLayout == null) {
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        mSwipeRefreshLayout.setRefreshing(true);

        DataStore.retrievePendingFriends(user.get(SessionManager.KEY_ID), user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new DataStore.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        View view = getView();
                        if (view == null || isDetached()) {
                            return;
                        }

                        mSwipeRefreshLayout.setRefreshing(false);

                        if (failed) {
                            if (getActivity() != null) {
                                showNoInternetActivity();
                            } else {
                                L.i("getActivity is null");
                            }
                            return;
                        }

                        mAdapter.updateData((List<UserDto>) result);
                    }
                });
    }

    private void onAcceptClicked(String friendId) {
        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        ((BaseActivity) getActivity()).showProgress("Loading...");

        DataStore.acceptFriendRequest(user.get(SessionManager.KEY_ID), friendId, user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new DataStore.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        View view = getView();
                        if (view == null || isDetached()) {
                            return;
                        }

                        if (failed) {
                            ((BaseActivity) getActivity()).hideProgress(false);
                        } else {
                            ((BaseActivity) getActivity()).hideProgress(true);

                            refreshFriends();
                        }
                    }
                });
    }

    private void refreshFriends() {
        doLoadPendingFriends();

        Fragment targetFragment = getTargetFragment();
        if(targetFragment != null && targetFragment instanceof OnFriendRequestAcceptedListener) {
            OnFriendRequestAcceptedListener listener = (OnFriendRequestAcceptedListener) targetFragment;
            listener.onFriendRequestAccepted();
        }
    }

    private void onDeclineClicked(String friendId) {
        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        ((BaseActivity) getActivity()).showProgress("Loading...");

        String userId = user.get(SessionManager.KEY_ID);
        String accessToken = user.get(SessionManager.KEY_ACCESS_TOCKEN);

        DataStore.declineFriendRequest(accessToken, userId, friendId, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                View view = getView();
                if (view == null || isDetached()) {
                    return;
                }

                if (failed) {
                    ((BaseActivity) getActivity()).hideProgress(false);
                } else {
                    ((BaseActivity) getActivity()).hideProgress(true);

                    doLoadPendingFriends();
                }
            }
        });
    }

    public interface OnFriendRequestAcceptedListener {
        public void onFriendRequestAccepted();
    }
}