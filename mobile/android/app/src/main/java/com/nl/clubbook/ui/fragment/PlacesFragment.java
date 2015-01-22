package com.nl.clubbook.ui.fragment;

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
import com.nl.clubbook.model.ClubbookPreferences;
import com.nl.clubbook.ui.adapter.PlacesAdapter;
import com.nl.clubbook.model.httpclient.HttpClientManager;
import com.nl.clubbook.model.data.Place;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class PlacesFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_PLACE_TYPE = "ARG_PLACE_TYPE";

    private PlacesAdapter mPlacesAdapter;

    public static Fragment newInstance(Fragment targetFragment, String placeType) {
        Fragment fragment = new PlacesFragment();
        fragment.setTargetFragment(targetFragment, 0);

        Bundle args = new Bundle();
        args.putString(ARG_PLACE_TYPE, placeType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_places, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initActionBarTitle(getString(R.string.going_out));
        initView();
        doRefresh(false, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            actionBar.setTitle(R.string.going_out);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = ClubFragment.newInstance(PlacesFragment.this, mPlacesAdapter.getItem(position));
        openFragment(fragment, ClubFragment.class);
    }

    @Override
    protected void loadData() {
        mSkipNumber = DEFAULT_CLUBS_SKIP;

        doRefresh(true, false);
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mPlacesAdapter = new PlacesAdapter(getActivity(), new ArrayList<Place>());

        final ListView clubList = (ListView) view.findViewById(R.id.listPlaces);
        clubList.addFooterView(mFooterProgress);

        clubList.setAdapter(mPlacesAdapter);
        clubList.setOnItemClickListener(this);
        clubList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && clubList.getLastVisiblePosition() >= clubList.getCount() - 1) {
                    mSkipNumber += DEFAULT_CLUBS_COUNT;

                    doRefresh(false, true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    public void doSearchPlaces() {
        mSkipNumber = DEFAULT_CLUBS_SKIP;

        doRefresh(true, false);
    }

    public void doRefresh() {
        mSkipNumber = DEFAULT_CLUBS_SKIP;

        doRefresh(true, false);
    }

    private void doRefresh(boolean isSwipeLayoutRefreshing, boolean isFooterVisible) {
        if(!NetworkUtils.isOn(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Location Updates", "Google Play services is available.");

        if(isSwipeLayoutRefreshing) {
            setProgressViewsState(true, View.GONE, View.GONE);
        } else if(isFooterVisible) {
            setProgressViewsState(false, View.VISIBLE, View.GONE);
        } else {
            setProgressViewsState(false, View.GONE, View.VISIBLE);
        }

        // retrieve my current location
        LocationCheckinHelper locationCheckInHelper = LocationCheckinHelper.getInstance();
        Location currentLocation = locationCheckInHelper.getCurrentLocation();
        if(currentLocation == null) {
            locationCheckInHelper.showLocationErrorView(getActivity(), locationCheckInHelper.isLocationProvidersEnabled());
            setProgressViewsState(false, View.GONE, View.GONE);

            return;
        }

        String accessToken = getSession().getUserDetails().get(ClubbookPreferences.KEY_ACCESS_TOCKEN);
        if(accessToken == null) {
            L.i("accessToken = null");

            setProgressViewsState(false, View.GONE, View.GONE);

            return;
        }

        String searchQuery = "";
        Fragment targetFragment = getTargetFragment();
        if(targetFragment instanceof OnGetSearchQueryListener) {
            OnGetSearchQueryListener listener = (OnGetSearchQueryListener) targetFragment;
            searchQuery = listener.getSearchQuery();
        }

        final String type = getArguments().getString(ARG_PLACE_TYPE, Types.ALL);

        // retrieve places from server and set distance
        HttpClientManager.getInstance().retrievePlaces(type, searchQuery, String.valueOf(mSkipNumber), String.valueOf(DEFAULT_CLUBS_COUNT), String.valueOf(currentLocation.getLatitude()),
                String.valueOf(currentLocation.getLongitude()), accessToken, new HttpClientManager.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        if (isDetached() || getActivity() == null) {
                            L.i("fragment_is_detached");
                            return;
                        }

                        setProgressViewsState(false, View.GONE, View.GONE);

                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                            return;
                        }

                        List<Place> places = (List<Place>) result;

                        if (mSkipNumber == DEFAULT_CLUBS_SKIP) {
                            mPlacesAdapter.updateData(places);
                        } else {
                            mPlacesAdapter.addData(places);
                        }
                    }
                });
    }

    public interface Types {
        public static final String ALL = "";
        public static final String CLUB = "club";
        public static final String BAR = "bar";
    }

    public interface OnGetSearchQueryListener {
        public String getSearchQuery();
    }
}
