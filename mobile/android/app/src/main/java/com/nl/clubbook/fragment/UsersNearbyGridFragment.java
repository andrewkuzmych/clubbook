package com.nl.clubbook.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.ProfileAdapter;
import com.nl.clubbook.datasource.HttpClientManager;
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
public class UsersNearbyGridFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener {

    public static final String ARG_REQUEST_TYPE = "ARG_REQUEST_TYPE";

    private ProfileAdapter mAdapter;
    private View mProgressBar;

    public static Fragment newInstance(Fragment targetFragment, String requestType) {
        Fragment fragment = new UsersNearbyGridFragment();
        fragment.setTargetFragment(targetFragment, 0);

        Bundle args = new Bundle();
        args.putString(ARG_REQUEST_TYPE, requestType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_checked_in_users, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendScreenStatistic(R.string.checked_in_users_android);

        initView();
        doRefresh(false, false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = ProfilePageHolderFragment.newInstance(this, mAdapter.getUsers(), position);
        openFromInnerFragment(fragment, ProfilePageHolderFragment.class);
    }

    @Override
    protected void loadData() {
        doRefresh(true, false);
    }

    public void refreshFragment() {
        doRefresh(true, false);
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mAdapter = new ProfileAdapter(getActivity(), new ArrayList<User>(), ProfileAdapter.MODE_DEFAULT);
        GridView gridUsers = (GridView) view.findViewById(R.id.gridUsers);
        gridUsers.setAdapter(mAdapter);
        gridUsers.setOnItemClickListener(this);

        mProgressBar = view.findViewById(R.id.progressBar);
    }

    private void doRefresh(boolean isSwipeToRefreshRefreshed, boolean isFooterRefreshed) {
        final View view = getView();
        if(view == null) {
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

        if(isSwipeToRefreshRefreshed) {
            setProgressViewsState(true, View.GONE, View.GONE);
        } else if(isFooterRefreshed) {
            setProgressViewsState(false, View.VISIBLE, View.GONE);
        } else {
            setProgressViewsState(false, View.GONE, View.VISIBLE);
        }

        String requestType = getArguments().getString(ARG_REQUEST_TYPE, RequestTypes.AROUND);
        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        Fragment targetFragment = getTargetFragment();
        String distanceKm = String.valueOf(UsersNearbyFragment.DISTANCES[UsersNearbyFragment.DEFAULT_DISTANCE]);
        if(targetFragment != null && targetFragment instanceof OnGetDistanceListener) {
            OnGetDistanceListener listener = (OnGetDistanceListener) targetFragment;
            distanceKm = String.valueOf(listener.onGetDistanceListener());
        }

        String gender = UsersNearbyFragment.Filter.ALL;
        if(targetFragment != null && targetFragment instanceof OnGetFilterListener) {
            OnGetFilterListener listener = (OnGetFilterListener) targetFragment;
            gender = listener.onGetFilter();
        }

        setOnRefreshing(true);

        HttpClientManager.getInstance().retrieveNearbyUsers(
                requestType,
                gender,
                user.get(SessionManager.KEY_ACCESS_TOCKEN),
                "" + location.getLatitude(),
                "" + location.getLongitude(),
                distanceKm,
                new HttpClientManager.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        View view = getView();
                        if (view == null || isDetached() || getActivity() == null) {
                            return;
                        }

                        setProgressViewsState(false, View.GONE, View.GONE);
                        setOnRefreshing(false);

                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                            return;
                        }

                        List<User> userList = (List<User>) result;
                        if (userList.size() > 0) {
                            view.findViewById(R.id.txtNoUsers).setVisibility(View.GONE);
                        } else if (mAdapter.getCount() == 0) {
                            view.findViewById(R.id.txtNoUsers).setVisibility(View.VISIBLE);
                        }

                        mAdapter.updateData(userList);
                    }
                });
    }

    private void setProgressViewsState(boolean isSwipeToRefreshRefreshed, int footerVisibility, int progressBarVisibility) {
        mSwipeRefreshLayout.setRefreshing(isSwipeToRefreshRefreshed);
        //TODO footer visibility
        mProgressBar.setVisibility(progressBarVisibility);
    }

    private void setOnRefreshing(boolean isEnabled) {
        Fragment targetFragment = getTargetFragment();
        if(targetFragment != null && targetFragment instanceof OnRefreshListener) {
            OnRefreshListener listener = (OnRefreshListener) targetFragment;
            listener.onRefresh(isEnabled);
        }
    }

    public interface RequestTypes {
        public static final String AROUND = "around";
        public static final String CHECKED_IN = "checkedin";
    }

    public interface OnGetDistanceListener {
        public int onGetDistanceListener();
    }

    public interface OnGetFilterListener {
        public String onGetFilter();
    }

    public interface OnRefreshListener {
        public void onRefresh(boolean isRefreshing);
    }
}
