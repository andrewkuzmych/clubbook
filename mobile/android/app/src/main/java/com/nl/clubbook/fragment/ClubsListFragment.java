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
import android.widget.SeekBar;
import android.widget.TextView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClubsListFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private ClubsAdapter mClubsAdapter;

    private int mCurrentDistance = SessionManager.DEFAULT_DISTANCE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_clubs_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initActionBarTitle(getString(R.string.club_list));
        initView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            initActionBarTitle(getString(R.string.club_list));
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

        String distanceKm = String.valueOf(mCurrentDistance);
        Log.d("Location Updates", "Google Play services is available.");

        // retrieve my current location
        Location currentLocation = LocationCheckinHelper.getCurrentLocation();
        if(currentLocation == null) {
            L.v("Location is empty");
            return;
        }

        View view = getView();
        if(view == null) {
            L.v("view == null!");
            return;
        }
        final View seekBarDistance = view.findViewById(R.id.seekBarDistance);
        seekBarDistance.setEnabled(false);

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
        DataStore.retrievePlaces(distanceKm, String.valueOf(currentLocation.getLatitude()),
                String.valueOf(currentLocation.getLongitude()), accessToken, new DataStore.OnResultReady() {

            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    seekBarDistance.setEnabled(true);
                    return;
                }
                // hide progress
                mSwipeRefreshLayout.setRefreshing(false);
                seekBarDistance.setEnabled(true);

                List<ClubDto> places = (List<ClubDto>) result;
                // sort by distance
                Collections.sort(places, new Comparator<ClubDto>() {
                    @Override
                    public int compare(ClubDto lhs, ClubDto rhs) {
                        if (lhs.getDistance() > rhs.getDistance()) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                });

                mClubsAdapter.updateData(places);
            }
        });
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        SeekBar seekBarDistance = (SeekBar) view.findViewById(R.id.seekBarDistance);
        seekBarDistance.setMax(9);
        seekBarDistance.incrementProgressBy(1);
        seekBarDistance.setProgress(mCurrentDistance);

        final TextView txtDistance = (TextView) view.findViewById(R.id.distance_text);
        txtDistance.setText(convertToKm(mCurrentDistance) + " " + getString(R.string.km));

        seekBarDistance.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        mCurrentDistance = convertToKm(progressValue);
                        progress = progressValue;
                        txtDistance.setText(String.valueOf(mCurrentDistance) + " " + getString(R.string.km));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here, if you want to do anything at the start of touching the seekbar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        loadData();
                    }
                }
        );

        mClubsAdapter = new ClubsAdapter(getActivity(), new ArrayList<ClubDto>());
        ListView clubList = (ListView) view.findViewById(R.id.listClub);
        clubList.setAdapter(mClubsAdapter);
        clubList.setOnItemClickListener(this);

        // load data based on selected distance to filter on
        mCurrentDistance = seekBarDistance.getProgress();
        loadData();
    }

    private int convertToKm(int value) {
        int result = 0;
        switch (value) {
            case 0:
                result = 1;
                break;
            case 1:
                result = 2;
                break;
            case 2:
                result = 3;
                break;
            case 3:
                result = 4;
                break;
            case 4:
                result = 5;
                break;
            case 5:
                result = 10;
                break;
            case 6:
                result = 20;
                break;
            case 7:
                result = 30;
                break;
            case 8:
                result = 50;
                break;
            case 9:
                result = 100;
                break;
            default:
                break;
        }
        return result;
    }
}
