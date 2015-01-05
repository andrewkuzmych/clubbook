package com.nl.clubbook.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.FastCheckInAdapter;
import com.nl.clubbook.datasource.HttpClientManager;
import com.nl.clubbook.datasource.Place;
import com.nl.clubbook.fragment.dialog.MessageDialog;
import com.nl.clubbook.fragment.dialog.ProgressDialog;
import com.nl.clubbook.helper.CheckInOutCallbackInterface;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodymyr on 11.11.2014.
 */
public class FastCheckInFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener,
        View.OnClickListener, MessageDialog.MessageDialogListener {

    private final int ACTION_ID_CHECK_IN_EXPLANATION = 753;

    private FastCheckInAdapter mAdapter;
    private Place mSelectedPlace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_fast_check_in, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendScreenStatistic(R.string.fast_check_in_android);

        initActionBarTitle(getString(R.string.fast_check_in));
        initView();
        doRefresh(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            actionBar.setTitle(R.string.clubs);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = ClubFragment.newInstance(FastCheckInFragment.this, mAdapter.getItem(position));
        openFragment(fragment, ClubFragment.class);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.txtCheckIn) {
            onCheckInBtnClicked(view);
        }
    }

    @Override
    public void onPositiveButtonClick(MessageDialog dialogFragment) {
        if(dialogFragment.getActionId() == ACTION_ID_CHECK_IN_EXPLANATION) {
            checkIn();
        }
    }

    @Override
    public void onNegativeButtonClick(MessageDialog dialogFragment) {
        dialogFragment.dismissAllowingStateLoss();
    }

    @Override
    protected void loadData() {
        doRefresh(true);
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mAdapter = new FastCheckInAdapter(getActivity(), new ArrayList<Place>(), this);
        ListView clubList = (ListView) view.findViewById(R.id.listPlaces);
        clubList.setAdapter(mAdapter);
        clubList.setOnItemClickListener(this);
    }

    private void doRefresh(boolean isPullToRefreshRefreshed) {
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

        final View view = getView();
        if(view == null) {
            L.v("view == null!");
            return;
        }

        final View progressBar = view.findViewById(R.id.progressBar);

        if(isPullToRefreshRefreshed) {
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

        String accessToken = getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN);
        if(accessToken == null) {
            mSwipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            L.i("accessToken = null");
            return;
        }

        // retrieve places from server and set distance
        HttpClientManager.getInstance().retrieveFastCheckInClub(String.valueOf(currentLocation.getLatitude()),
                String.valueOf(currentLocation.getLongitude()), "1", accessToken, new HttpClientManager.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        if (isDetached() || getActivity() == null) {
                            L.i("fragment_is_detached");
                            return;
                        }

                        mSwipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);

                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                            showHideEmptyView(view);

                            return;
                        }

                        List<Place> places = (List<Place>) result;
                        mAdapter.updateData(places);
                        showHideEmptyView(view);
                    }
                });
    }

    private void showHideEmptyView(View view) {
        TextView txtNoFastCheckIn = (TextView) view.findViewById(R.id.txtNoFastCheckIn);
        if(mAdapter.getCount() > 0) {
            txtNoFastCheckIn.setVisibility(View.GONE);
        } else {
            txtNoFastCheckIn.setVisibility(View.VISIBLE);
        }
    }

    private void onCheckInBtnClicked(final View view) {
        mSelectedPlace = (Place) view.getTag();

        if(!LocationCheckinHelper.getInstance().canCheckInHere(mSelectedPlace)) {
            String messageTemplate = getString(R.string.you_need_to_be_within_m_in_order_to_check_in);
            String dialogMessage = String.format(messageTemplate, SessionManager.getInstance().getCheckInMaxDistance());

            showMessageDialog(dialogMessage, getString(R.string.ok));
            return;
        }

        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        if (LocationCheckinHelper.getInstance().isCheckInHere(mSelectedPlace)) {
            showProgressDialog(getString(R.string.checking_out));

            LocationCheckinHelper.getInstance().checkOut(getActivity(), new CheckInOutCallbackInterface() {
                @Override
                public void onCheckInOutFinished(boolean isSuccess) {
                    if(!isSuccess) {
                        showToast(R.string.something_went_wrong_please_try_again);
                        return;
                    }

                    handleCheckInCheckOutResults();
                }
            });
        } else {
            showProgressDialog(getString(R.string.checking_in));

            if(!getSession().isCheckInDialogShown()) {
                showMessageDialog(
                        FastCheckInFragment.this,
                        ACTION_ID_CHECK_IN_EXPLANATION,
                        getString(R.string.app_name),
                        getString(R.string.you_will_be_automatically_checked_in_to_this_club),
                        getString(R.string.ok_got_it),
                        null
                );

                getSession().setCheckInDialogShown(true);
            } else {
                checkIn(view);
            }
        }
    }

    private void checkIn() {
        View view = getView();
        if(view == null) {
            return;
        }

        View txtCheckIn = view.findViewById(R.id.txtCheckIn);
        checkIn(txtCheckIn);
    }

    private void checkIn(final View view) {
        LocationCheckinHelper.getInstance().checkIn(getActivity(), mSelectedPlace, new CheckInOutCallbackInterface() {
            @Override
            public void onCheckInOutFinished(boolean isSuccess) {
                if(!isSuccess) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    return;
                }

                handleCheckInCheckOutResults();
            }
        });
    }

    private void handleCheckInCheckOutResults() {
        if(isDetached() || getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        hideProgressDialog();

        mAdapter.notifyDataSetChanged();
    }

    private void showProgressDialog(String message) {
        Fragment dialogFragment = ProgressDialog.newInstance(FastCheckInFragment.this, getString(R.string.app_name), message);
        getChildFragmentManager().beginTransaction().add(dialogFragment, ProgressDialog.TAG).commitAllowingStateLoss();
    }

    private void hideProgressDialog() {
        DialogFragment dialogFragment = (DialogFragment)getChildFragmentManager().findFragmentByTag(ProgressDialog.TAG);
        if(dialogFragment != null) {
            dialogFragment.dismissAllowingStateLoss();
        }
    }
}
