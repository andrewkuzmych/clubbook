package com.nl.clubbook.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.ClubsAdapter;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class ClubsListFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private ClubsAdapter mClubsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_clubs_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initActionBarTitle(getString(R.string.clubs));
        initView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            initActionBarTitle(getString(R.string.clubs));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        View txtClubTitle = view.findViewById(R.id.txtClubName);
        String clubId = (String)txtClubTitle.getTag();

        Fragment fragment = ClubFragment.newInstance(ClubsListFragment.this, clubId);
        openFragment(fragment, ClubFragment.class);
    }

    @Override
    protected void loadData() {
        if(!NetworkUtils.isOn(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Location Updates", "Google Play services is available.");

        // retrieve my current location
        Location currentLocation = LocationCheckinHelper.getInstance().getCurrentLocation();
        if(currentLocation == null) {
            showNoLocationActivity();
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
        DataStore.retrievePlaces(String.valueOf(currentLocation.getLatitude()),
                String.valueOf(currentLocation.getLongitude()), accessToken, new DataStore.OnResultReady() {

            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    return;
                }
                // hide progress
                mSwipeRefreshLayout.setRefreshing(false);

                List<ClubDto> places = (List<ClubDto>) result;
                mClubsAdapter.updateData(places);
            }
        });
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mClubsAdapter = new ClubsAdapter(getActivity(), new ArrayList<ClubDto>());
        ListView clubList = (ListView) view.findViewById(R.id.listClub);
        clubList.setAdapter(mClubsAdapter);
        clubList.setOnItemClickListener(this);

        loadData();
    }
}
