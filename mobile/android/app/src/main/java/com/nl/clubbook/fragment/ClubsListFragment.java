package com.nl.clubbook.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.ClubsAdapter;
import com.nl.clubbook.datasource.Club;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class ClubsListFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private final int DEFAULT_CLUBS_COUNT = 20;
    private final int DEFAULT_CLUBS_SKIP = 0;

    private ClubsAdapter mClubsAdapter;
    private View mFooterProgress;

    private int mSkipNumber = DEFAULT_CLUBS_SKIP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_clubs_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendScreenStatistic(R.string.main_screen_android);

        initActionBarTitle(getString(R.string.clubs));
        initView();
        doRefresh(true);
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
        Fragment fragment = ClubFragment.newInstance(ClubsListFragment.this, mClubsAdapter.getItem(position));
        openFragment(fragment, ClubFragment.class);
    }

    @Override
    protected void loadData() {
        mSkipNumber = DEFAULT_CLUBS_SKIP;

        doRefresh(true);
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mClubsAdapter = new ClubsAdapter(getActivity(), new ArrayList<Club>());
        mFooterProgress = LayoutInflater.from(getActivity()).inflate(R.layout.view_footer_progress, null);
        mFooterProgress.setVisibility(View.INVISIBLE);

        final ListView clubList = (ListView) view.findViewById(R.id.listClub);
        clubList.addFooterView(mFooterProgress);

        clubList.setAdapter(mClubsAdapter);
        clubList.setOnItemClickListener(this);
        clubList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && clubList.getLastVisiblePosition() >= clubList.getCount() - 1) {
                    mSkipNumber += DEFAULT_CLUBS_COUNT;

                    doRefresh(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private void doRefresh(boolean isSwipeLayoutRefreshing) {
        if(!NetworkUtils.isOn(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Location Updates", "Google Play services is available.");

        if(isSwipeLayoutRefreshing) {
            mSwipeRefreshLayout.setRefreshing(true);
            mFooterProgress.setVisibility(View.GONE);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            mFooterProgress.setVisibility(View.VISIBLE);
        }

        // retrieve my current location
        LocationCheckinHelper locationCheckInHelper = LocationCheckinHelper.getInstance();
        Location currentLocation = locationCheckInHelper.getCurrentLocation();
        if(currentLocation == null) {
            locationCheckInHelper.showLocationErrorView(getActivity(), locationCheckInHelper.isLocationProvidersEnabled());

            mSwipeRefreshLayout.setRefreshing(false);
            mFooterProgress.setVisibility(View.GONE);
            return;
        }

        String accessToken = getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN);
        if(accessToken == null) {
            L.i("accessToken = null");

            mSwipeRefreshLayout.setRefreshing(false);
            mFooterProgress.setVisibility(View.GONE);

            return;
        }

        // retrieve places from server and set distance
        DataStore.retrievePlaces(String.valueOf(mSkipNumber), String.valueOf(DEFAULT_CLUBS_COUNT), String.valueOf(currentLocation.getLatitude()),
                String.valueOf(currentLocation.getLongitude()), accessToken, new DataStore.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        if(isDetached() || getActivity() == null) {
                            L.i("fragment_is_detached");
                            return;
                        }

                        mSwipeRefreshLayout.setRefreshing(false);
                        mFooterProgress.setVisibility(View.GONE);

                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                            return;
                        }

                        List<Club> places = (List<Club>) result;

                        if(mSkipNumber == DEFAULT_CLUBS_SKIP) {
                            mClubsAdapter.updateData(places);
                        } else {
                            mClubsAdapter.addData(places);
                        }
                    }
                });
    }
}
