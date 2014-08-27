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
import android.widget.TabHost;
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
import java.util.List;

public class ClubsListFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, TabHost.OnTabChangeListener {

    private ClubsAdapter mClubsAdapter;
    private int mMode = ClubsAdapter.MODE_NEARBY;

    private int mCurrentDistance = SessionManager.DEFAULT_DISTANCE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_clubs_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initActionBarTitle(getString(R.string.club_list));
        initTabHost();
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
    public void onTabChanged(String tabId) {
        String aZ = getString(R.string.a_z);

        if(aZ.equalsIgnoreCase(tabId)) {
            sortByName();
        } else {
            sortByDistance();
        }
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
        Location currentLocation = LocationCheckinHelper.getInstance().getCurrentLocation();
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

                mClubsAdapter.updateData(places, mMode);
            }
        });
    }

    private void initTabHost() {
        View view = getView();
        if(view == null) {
            return;
        }

        TabHost tabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        tabHost.setup();

        tabHost.addTab(newTabSpec(tabHost, getString(R.string.nearby)));
        tabHost.addTab(newTabSpec(tabHost, getString(R.string.a_z)));

        tabHost.setOnTabChangedListener(ClubsListFragment.this);
    }

    private TabHost.TabSpec newTabSpec(TabHost tabHost, String tabIndicator) {
        View tabIndicatorView = LayoutInflater.from(getActivity()).inflate(R.layout.apptheme_tab_indicator_holo, tabHost.getTabWidget(), false);
        TextView title = (TextView) tabIndicatorView.findViewById(android.R.id.title);
        title.setText(tabIndicator);

        return tabHost.newTabSpec(tabIndicator).setContent(android.R.id.tabcontent).setIndicator(tabIndicatorView);
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

    private void sortByName() {
        mMode = ClubsAdapter.MODE_A_Z;
        mClubsAdapter.sortByName();
    }

    private void sortByDistance() {
        mMode = ClubsAdapter.MODE_NEARBY;
        mClubsAdapter.sortByDistance();
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

//    private void test() {
//        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new MyGestureDetector());
//
//        View.OnTouchListener gestureListener =  new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                return gestureDetector.onTouchEvent(event);
//            }
//        };
//
//        ListView listView = (ListView) getView().findViewById(R.id.listClub);
//        listView.setOnTouchListener(gestureListener);
//    }
//
//    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
//        private static final int SWIPE_MIN_DISTANCE = 120;
//        private static final int SWIPE_MAX_OFF_PATH = 250;
//        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            try {
//                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
//                    return false;
//                // right to left swipe
//                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Toast.makeText(getActivity(), "Left Swipe", Toast.LENGTH_SHORT).show();
//                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Toast.makeText(getActivity(), "Right Swipe", Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//                // nothing
//            }
//            return false;
//        }
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return true;
//        }
//    }
}
