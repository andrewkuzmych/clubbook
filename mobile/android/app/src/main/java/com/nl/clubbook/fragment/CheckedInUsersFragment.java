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
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.ProfileAdapter;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.User;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Volodymyr on 05.11.2014.
 */
public class CheckedInUsersFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener {

    private final int[] DISTANCES = new int[] { 1, 2, 3, 4, 5, 10, 15, 20 };
    private final int DEFAULT_DISTANCE = DISTANCES.length - 1;

    private ProfileAdapter mAdapter;
    private SeekBar mSeekBar;
    private int mCurrentProgress = DEFAULT_DISTANCE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_checked_in_users, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initActionBarTitle(getString(R.string.checked_in_));
        initView();
        loadData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            actionBar.setIcon(R.drawable.icon_play);
            actionBar.setTitle(R.string.checked_in_);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = ProfileFragment.newInstance(CheckedInUsersFragment.this, mAdapter.getItem(position), ProfileFragment.OPEN_MODE_DEFAULT);
        openFragment(fragment, ProfileFragment.class);
    }

    @Override
    protected void loadData() {
        final View view = getView();
        if(view == null || mSwipeRefreshLayout == null) {
            return;
        }

        if(!NetworkUtils.isOn(getActivity())) {
            if(mAdapter.getCount() == 0) {
                view.findViewById(R.id.txtNoFriendsAdded).setVisibility(View.VISIBLE);
            }
            showToast(R.string.no_connection);
            return;
        }

        Location location = LocationCheckinHelper.getInstance().getCurrentLocation();
        if(location == null) {
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();
        final String distanceKm = String.valueOf(DISTANCES[mCurrentProgress ]);

        mSwipeRefreshLayout.setRefreshing(true);
        mSeekBar.setEnabled(false);

        DataStore.retrieveCurrentCheckedInUsers(
                user.get(SessionManager.KEY_ACCESS_TOCKEN),
                "" + location.getLatitude(),
                "" + location.getLongitude(),
                distanceKm,
                new DataStore.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        View view = getView();
                        if (view == null || isDetached() || getActivity() == null) {
                            return;
                        }

                        mSwipeRefreshLayout.setRefreshing(false);
                        mSeekBar.setEnabled(true);

                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                            return;
                        }

                        List<User> userList = (List<User>) result;
                        if (userList.size() > 0) {
                            view.findViewById(R.id.txtNoUsers).setVisibility(View.GONE);
                        } else if(mAdapter.getCount() == 0) {
                            view.findViewById(R.id.txtNoUsers).setVisibility(View.VISIBLE);
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

        GridView gridUsers = (GridView) view.findViewById(R.id.gridUsers);
        gridUsers.setOnItemClickListener(this);
        mAdapter = new ProfileAdapter(getActivity(), new ArrayList<User>(), ProfileAdapter.MODE_DEFAULT);
        gridUsers.setAdapter(mAdapter);

        mSeekBar = (SeekBar) view.findViewById(R.id.seekBarDistance);
        mSeekBar.setMax(DISTANCES.length - 1);
        mSeekBar.incrementProgressBy(1);
        mSeekBar.setProgress(mCurrentProgress);

        final TextView txtDistance = (TextView) view.findViewById(R.id.distance_text);
        txtDistance.setText(DISTANCES[mCurrentProgress] + " " + getString(R.string.km));

        mSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        mCurrentProgress = progressValue;
                        txtDistance.setText(DISTANCES[mCurrentProgress] + " " + getString(R.string.km));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        loadData();
                    }
                }
        );

        mCurrentProgress = mSeekBar.getProgress();
    }
}
