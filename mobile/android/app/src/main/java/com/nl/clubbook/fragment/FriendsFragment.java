package com.nl.clubbook.fragment;

import android.os.Bundle;
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

public class FriendsFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener {

    private ListView mListFriends;
    private FriendsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
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
        openFragment(new ProfileFragment(FriendsFragment.this, userId, null));
    }

    @Override
    protected void loadData() {
        final View view = getView();
        if(view == null || mListFriends == null || mSwipeRefreshLayout == null) {
            return;
        }

        final SessionManager session = new SessionManager(getActivity());
        final HashMap<String, String> user = session.getUserDetails();

        mSwipeRefreshLayout.setRefreshing(true);

        DataStore.retrieveFriends(user.get(SessionManager.KEY_ID), user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new DataStore.OnResultReady() {

            @Override
            public void onReady(Object result, boolean failed) {
                View view = getView();
                if(view == null || isDetached()) {
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
        mListFriends.setOnItemClickListener(FriendsFragment.this);
    }
}