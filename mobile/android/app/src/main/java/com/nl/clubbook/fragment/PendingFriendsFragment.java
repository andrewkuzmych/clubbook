package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.PendingFriendsAdapter;
import com.nl.clubbook.datasource.HttpClientManager;
import com.nl.clubbook.datasource.User;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.NetworkUtils;

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
        doLoadPendingFriends(false);
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
        Fragment fragment = ProfilePageHolderFragment.newInstance(this, mAdapter.getUsers(), position);
        openFromInnerFragment(fragment, ProfilePageHolderFragment.class);
    }

    @Override
    public void onClick(View v) {
        String userId = (String)v.getTag();

        switch (v.getId()) {
            case R.id.txtAccept:
                onAcceptClicked(userId);
                break;
            case R.id.txtDecline:
                onDeclineClicked(userId);
                break;
        }
    }

    @Override
    protected void loadData() {
        doLoadPendingFriends(true);
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mAdapter = new PendingFriendsAdapter(getActivity(), new ArrayList<User>(), this);
        ListView listPendingFriends = (ListView) view.findViewById(R.id.listPendingFriends);
        listPendingFriends.setAdapter(mAdapter);
        listPendingFriends.setOnItemClickListener(this);
    }

    private void doLoadPendingFriends(boolean isSwipeToRefreshRefreshed) {
        final View view = getView();
        if(view == null || mSwipeRefreshLayout == null) {
            return;
        }

        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        if(isSwipeToRefreshRefreshed) {
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        HttpClientManager.getInstance().retrievePendingFriends(user.get(SessionManager.KEY_ID), user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new HttpClientManager.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        View view = getView();
                        if (view == null || isDetached() || getActivity() == null) {
                            return;
                        }

                        mSwipeRefreshLayout.setRefreshing(false);
                        view.findViewById(R.id.progressBar).setVisibility(View.GONE);

                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                            return;
                        }

                        mAdapter.updateData((List<User>) result);
                    }
                });
    }

    private void onAcceptClicked(String friendId) {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        showProgress(getString(R.string.loading));

        HttpClientManager.getInstance().acceptFriendRequest(user.get(SessionManager.KEY_ID), friendId, user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new HttpClientManager.OnResultReady() {

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
                            refreshFriends();
                        }
                    }
                });
    }

    private void refreshFriends() {
        doLoadPendingFriends(true);

        Fragment targetFragment = getTargetFragment();
        if(targetFragment != null && targetFragment instanceof OnFriendRequestAcceptedListener) {
            OnFriendRequestAcceptedListener listener = (OnFriendRequestAcceptedListener) targetFragment;
            listener.onFriendRequestAccepted();
        }
    }

    private void onDeclineClicked(String friendId) {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        showProgress(getString(R.string.loading));

        String userId = user.get(SessionManager.KEY_ID);
        String accessToken = user.get(SessionManager.KEY_ACCESS_TOCKEN);

        HttpClientManager.getInstance().declineFriendRequest(accessToken, userId, friendId, new HttpClientManager.OnResultReady() {
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
                    doLoadPendingFriends(true);
                }
            }
        });
    }

    public interface OnFriendRequestAcceptedListener {
        public void onFriendRequestAccepted();
    }
}
