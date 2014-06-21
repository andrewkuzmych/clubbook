package com.nl.clubbook.fragment;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.adapter.ClubsAdapter;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.helper.SessionManager;

import java.util.Comparator;
import java.util.List;

public class HomeFragment extends BaseFragment {

    private SeekBar distance;
    ListView club_list;
    private int index = -1;
    private int top = 0;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        distance = (SeekBar) rootView.findViewById(R.id.distance);
        distance.setMax(9);
        distance.incrementProgressBy(1);
        distance.setProgress(SessionManager.DEFOULT_DISTANCE);

        final TextView distance_text = (TextView) rootView.findViewById(R.id.distance_text);
        distance_text.setText(convertToKm(SessionManager.DEFOULT_DISTANCE) + " " + getString(R.string.km));

        distance.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;
                    int km = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                        km = convertToKm(progresValue);
                        progress = progresValue;
                        distance_text.setText(String.valueOf(km) + " " + getString(R.string.km));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here,
                        //if you want to do anything at the start of
                        // touching the seekbar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        loadData(String.valueOf(km));
                    }
                }
        );

        club_list = (ListView) rootView.findViewById(R.id.club_listview);
        // load data based on selected distance to filter on
        loadData(getSelectedDistance());
        return rootView;
    }

    protected void loadData(String distanceKm) {
        final Context contextThis = getActivity();
        final BaseFragment thisInstance = this;

        Log.d("Location Updates", "Google Play services is available.");

        // retrieve my current location
        Location currentLocation = LocationCheckinHelper.getCurrentLocation();
        // show progress
        ((BaseActivity) getActivity()).showProgress("Loading...");
        // retrieve places from server and set distance
        DataStore.retrievePlaces(distanceKm, String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    ((BaseActivity) getActivity()).hideProgress(false);
                    return;
                }
                // hide progress
                ((BaseActivity) getActivity()).hideProgress(true);
                // prepare adapter
                List<ClubDto> places = (List<ClubDto>) result;
                DataStore.setPlaceAdapter(new ClubsAdapter(contextThis, R.layout.club_list_item, places.toArray(new ClubDto[places.size()])));
                // sort by distance
                DataStore.getPlaceAdapter().sort(new Comparator<ClubDto>() {
                    @Override
                    public int compare(ClubDto lhs, ClubDto rhs) {
                        if (lhs.getDistance() > rhs.getDistance()) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                });
                club_list.setAdapter(DataStore.getPlaceAdapter());
                // set event handler to open club details
                club_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String club_id = ((TextView) view.findViewById(R.id.club_id)).getText().toString();
                        ClubFragment fragment = new ClubFragment(thisInstance, club_id);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
                        mFragmentTransaction.addToBackStack(null);
                        mFragmentTransaction.replace(R.id.frame_container, fragment).commit();
                    }
                });
                // scroll to clicked club when you return from club details by clicking back button
                if (index != -1) {
                    club_list.setSelectionFromTop(index, top);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            index = club_list.getFirstVisiblePosition();
            View v = club_list.getChildAt(0);
            top = (v == null) ? 0 : v.getTop();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public String getSelectedDistance() {
        return String.valueOf(convertToKm(distance.getProgress()));
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
