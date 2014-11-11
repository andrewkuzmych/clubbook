package com.nl.clubbook.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.FastCheckInAdapter;
import com.nl.clubbook.datasource.Club;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodymyr on 11.11.2014.
 */
public class FastCheckInFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener {

    private FastCheckInAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_fast_chekc_in, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendScreenStatistic(R.string.fast_check_in_android);

        initActionBarTitle(getString(R.string.clubs));
        initView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            actionBar.setIcon(R.drawable.icon_play);
            actionBar.setTitle(R.string.clubs);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = ClubFragment.newInstance(FastCheckInFragment.this, mAdapter.getItem(position));
        openFragment(fragment, ClubFragment.class);
    }

    @Override
    protected void loadData() {
        if(!NetworkUtils.isOn(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        // retrieve my current location
        LocationCheckinHelper locationCheckInHelper = LocationCheckinHelper.getInstance();
        Location currentLocation = locationCheckInHelper.getCurrentLocation();
        if(currentLocation == null) {
            locationCheckInHelper.showLocationErrorView(getActivity(), locationCheckInHelper.isLocationProvidersEnabled());
            return;
        }

        View view = getView();
        if(view == null) {
            L.v("view == null!");
            return;
        }

        if(!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }

        String accessToken = getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN);
        if(accessToken == null) {
            mSwipeRefreshLayout.setRefreshing(false);
            L.i("accessToken = null");
            return;
        }

        // retrieve places from server and set distance
        DataStore.retrieveFastCheckInClub(String.valueOf(currentLocation.getLatitude()),
                String.valueOf(currentLocation.getLongitude()), "1", accessToken, new DataStore.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        if (isDetached() || getActivity() == null) {
                            L.i("fragment_is_detached");
                            return;
                        }

                        mSwipeRefreshLayout.setRefreshing(false);
                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                            return;
                        }

                        List<Club> places = (List<Club>) result;
                        mAdapter.updateData(places);
                    }
                });
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mAdapter = new FastCheckInAdapter(getActivity(), new ArrayList<Club>());
        ListView clubList = (ListView) view.findViewById(R.id.listClub);
        clubList.setAdapter(mAdapter);
        clubList.setOnItemClickListener(this);

        loadData();
    }
}
