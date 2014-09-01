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

        initView();
        loadData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String userId = view.findViewById(R.id.txtUsername).getTag().toString();
        Fragment fragment = ProfileFragment.newInstance(getTargetFragment(), userId, null, ProfileFragment.OPEN_MODE_DEFAULT);
        openFragment(fragment, ProfileFragment.class);
    }

    @Override
    protected void loadData() {
        final View view = getView();
        if(view == null || mListFriends == null || mSwipeRefreshLayout == null) {
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        mSwipeRefreshLayout.setRefreshing(true);

        DataStore.retrieveFriends(user.get(SessionManager.KEY_ID), user.get(SessionManager.KEY_ACCESS_TOCKEN),
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

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mAdapter = new FriendsAdapter(getActivity(), new ArrayList<UserDto>());
        mListFriends = (ListView) view.findViewById(R.id.listFriends);
        mListFriends.setAdapter(mAdapter);
        mListFriends.setOnItemClickListener(FriendListFragment.this);
    }
}
