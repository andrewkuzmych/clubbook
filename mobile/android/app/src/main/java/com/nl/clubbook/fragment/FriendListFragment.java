package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.FriendsAdapter;
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
public class FriendListFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener {

    private ListView mListFriends;
    private FriendsAdapter mAdapter;

    public static Fragment newInstance(Fragment targetFragment) {
        Fragment fragment = new FriendListFragment();
        fragment.setTargetFragment(targetFragment, 0);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_friends_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendScreenStatistic(R.string.friends_screen_android);

        initView();
        loadData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = ProfilePageHolderFragment.newInstance(this, mAdapter.getUsers(), position);
        openFromInnerFragment(fragment, ProfilePageHolderFragment.class);
    }

    @Override
    protected void loadData() {
        final View view = getView();
        if(view == null || mListFriends == null || mSwipeRefreshLayout == null) {
            return;
        }

        if(!NetworkUtils.isOn(getActivity())) {
            if(mAdapter.getCount() == 0) {
                view.findViewById(R.id.txtNoFriendsAdded).setVisibility(View.VISIBLE);
            }
            showToast(R.string.no_connection);
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        mSwipeRefreshLayout.setRefreshing(true);

        HttpClientManager.getInstance().retrieveFriends(user.get(SessionManager.KEY_ID), user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new HttpClientManager.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        View view = getView();
                        if (view == null || isDetached() || getActivity() == null) {
                            return;
                        }

                        mSwipeRefreshLayout.setRefreshing(false);

                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                            return;
                        }

                        List<User> userList = (List<User>) result;
                        if (userList.size() > 0) {
                            view.findViewById(R.id.txtNoFriendsAdded).setVisibility(View.GONE);
                        } else {
                            view.findViewById(R.id.txtNoFriendsAdded).setVisibility(View.VISIBLE);
                        }

                        mAdapter.updateData(userList);
                    }
                });
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mAdapter = new FriendsAdapter(getActivity(), new ArrayList<User>());
        mListFriends = (ListView) view.findViewById(R.id.listFriends);
        mListFriends.setAdapter(mAdapter);
        mListFriends.setOnItemClickListener(FriendListFragment.this);
    }
}
