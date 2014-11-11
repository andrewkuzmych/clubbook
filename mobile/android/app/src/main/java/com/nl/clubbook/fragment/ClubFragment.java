package com.nl.clubbook.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.ClubInfoActivity;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.activity.YesterdayUsersGridActivity;
import com.nl.clubbook.adapter.ProfileAdapter;
import com.nl.clubbook.datasource.Club;
import com.nl.clubbook.datasource.ClubWorkingHours;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.JSONConverter;
import com.nl.clubbook.datasource.User;
import com.nl.clubbook.fragment.dialog.MessageDialog;
import com.nl.clubbook.fragment.dialog.ProgressDialog;
import com.nl.clubbook.helper.*;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;
import com.nl.clubbook.utils.UIUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andrew on 6/8/2014.
 */
public class ClubFragment extends BaseInnerFragment implements View.OnClickListener, AdapterView.OnItemClickListener,
        ProgressDialog.OnDialogCanceledListener, MessageDialog.MessageDialogListener {

    public static final String TAG = ClubFragment.class.getSimpleName();

    private static final String ARG_CLUB = "ARG_CLUB";

    private final int ACTION_ID_CAN_NOT_CHECK_IN = 357;
    private final int ACTION_ID_CHECK_IN_EXPLANATION = 753;

    private Club mClub;
    private ProfileAdapter mProfileAdapter;

    private boolean mIsLoading = false;
    private boolean mCanCheckInHere = false;

    public static Fragment newInstance(Fragment targetFragment, Club club) {
        Fragment fragment = new ClubFragment();
        fragment.setTargetFragment(targetFragment, 0);

        Bundle args = new Bundle();
        args.putParcelable(ARG_CLUB, club);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_club, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendScreenStatistic(R.string.club_users_screen_android);

        initTarget();

        handleArgs();

        UIUtils.displayEmptyIconInActionBar((ActionBarActivity) getActivity());
        initActionBarTitle(getString(R.string.checked_in));
        initView();
        loadCheckedInUsers();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            initActionBarTitle(getString(R.string.club_page));
            UIUtils.loadPhotoToActionBar((ActionBarActivity)getActivity(), ImageHelper.getUserListAvatar(mClub.getAvatar()), mTarget);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtCheckIn:
                onCheckInBtnClicked(view);
                break;
            case R.id.txtYesterday:
                onYesterdayClicked();
                break;
            case R.id.holderClubInfo:
                onHolderClubInfoClicked();
                break;
            case R.id.txtDistance:
                onDistanceClicked();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!LocationCheckinHelper.getInstance().isCheckInHere(mClub)) {
            showToast(R.string.need_to_check_in_first);
            return;
        }

        Fragment fragment = ProfileFragment.newInstance(ClubFragment.this, mProfileAdapter.getItem(position), ProfileFragment.OPEN_MODE_DEFAULT);
        openFragment(fragment, ProfileFragment.class);
    }

    @Override
    public void onDialogCanceled() {
        ActionBarActivity activity = (ActionBarActivity) getActivity();
        if(activity != null && !activity.isFinishing()) {
            activity.getSupportFragmentManager().beginTransaction().remove(ClubFragment.this).commitAllowingStateLoss();

            Fragment targetFragment = getTargetFragment();
            if(targetFragment != null) {
                activity.getSupportFragmentManager().beginTransaction().show(targetFragment).commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onPositiveButtonClick(MessageDialog dialogFragment) {
        switch(dialogFragment.getActionId()) {
            case ACTION_ID_CAN_NOT_CHECK_IN:
                dialogFragment.dismissAllowingStateLoss();
                break;
            case ACTION_ID_CHECK_IN_EXPLANATION:
                checkIn();
                break;
        }
    }

    @Override
    public void onNegativeButtonClick(MessageDialog dialogFragment) {
        dialogFragment.dismissAllowingStateLoss();
    }

    private void handleArgs() {
        Bundle args = getArguments();
        if(args == null) {
            return;
        }

        mClub = args.getParcelable(ARG_CLUB);
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        view.findViewById(R.id.txtCheckIn).setOnClickListener(this);
        view.findViewById(R.id.holderClubInfo).setOnClickListener(this);
        view.findViewById(R.id.txtYesterday).setOnClickListener(this);
        view.findViewById(R.id.txtDistance).setOnClickListener(this);

        GridView gridUsers = (GridView) view.findViewById(R.id.gridUsers);
        gridUsers.setOnItemClickListener(ClubFragment.this);
        mProfileAdapter = new ProfileAdapter(getActivity(), new ArrayList<User>(), ProfileAdapter.MODE_DEFAULT);
        gridUsers.setAdapter(mProfileAdapter);

        fillView(view);
    }

    public void onClubCheckedOut() {
        View view = getView();
        if(view == null) {
            return;
        }

        handleCheckInCheckOutResults(view, false);

        loadCheckedInUsers();
    }

    protected void loadCheckedInUsers() {
        final View view = getView();
        if(view == null || mClub == null || TextUtils.isEmpty(mClub.getId())) {
            return;
        }

        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        final HashMap<String, String> user = this.getSession().getUserDetails();

        setProgressViewState(true);
        view.findViewById(R.id.txtNoUsers).setVisibility(View.GONE);
        view.findViewById(R.id.gridUsers).setVisibility(View.GONE);

        DataStore.retrieveClubCheckedInUsers(mClub.getId(), user.get(SessionManager.KEY_ACCESS_TOCKEN), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                View view = getView();
                if(view == null || isDetached() || getActivity() == null || result == null) {
                    return;
                }

                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    view.findViewById(R.id.txtNoUsers).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.gridUsers).setVisibility(View.GONE);
                    return;
                }

                setProgressViewState(false);
                view.findViewById(R.id.gridUsers).setVisibility(View.VISIBLE);

                fillCheckedInUsers(view, (List<User>) result);
            }
        });
    }

    private void fillView(@NotNull View view) {
        if (mClub == null) {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            return;
        }

        TextView txtCheckIn = (TextView) view.findViewById(R.id.txtCheckIn);
        TextView txtClubName = (TextView) view.findViewById(R.id.txtClubName);
        TextView txtOpenToday = (TextView) view.findViewById(R.id.txtOpenToday);
        TextView txtCheckInCount = (TextView) view.findViewById(R.id.txtCheckInCount);
        TextView txtDistance = (TextView) view.findViewById(R.id.txtDistance);
        TextView txtFriendsCount = (TextView) view.findViewById(R.id.txtFriendsCount);
        ImageView imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);

        // if we checked in this this club set related style
        boolean isCheckedInHere = LocationCheckinHelper.getInstance().isCheckInHere(mClub);
        if (isCheckedInHere) {
            mCanCheckInHere = true;
            UiHelper.changeCheckInState(getActivity(), txtCheckIn, true);
        } else {
            UiHelper.changeCheckInState(getActivity(), txtCheckIn, false);
        }

        // can we check in this club
        if(!isCheckedInHere) {
            if (LocationCheckinHelper.getInstance().canCheckInHere(mClub)) {
                txtCheckIn.setBackgroundResource(R.drawable.bg_btn_green);
                mCanCheckInHere = true;
            } else {
                txtCheckIn.setBackgroundResource(R.drawable.bg_btn_green_transparent);
                mCanCheckInHere = false;
            }
        }


        setCheckInTxtPaddings(txtCheckIn);

        txtClubName.setText(mClub.getTitle());
        txtCheckInCount.setText(mClub.getActiveCheckIns() + "\n" + getString(R.string.checked_in));
        txtFriendsCount.setText(mClub.getActiveFriendsCheckIns() + "\n" + getString(R.string.friends));
        txtDistance.setText(LocationCheckinHelper.formatDistance(getActivity().getApplicationContext(), mClub.getDistance()));

        ClubWorkingHours workingHours = mClub.getTodayWorkingHours();
        if (workingHours != null) {
            if (ClubWorkingHours.STATUS_OPENED.equalsIgnoreCase(workingHours.getStatus())) {
                String startTime = workingHours.getStartTime();
                String endTime = workingHours.getEndTime();

                txtOpenToday.append(startTime != null ? startTime + " - " : "");
                txtOpenToday.append(endTime != null ? endTime : "");
            } else {
                txtOpenToday.setVisibility(View.GONE);
            }
        } else {
            txtOpenToday.setVisibility(View.GONE);
        }

        String avatarUrl = mClub.getAvatar();
        if (avatarUrl != null && avatarUrl.length() > 0) {
            Picasso.with(getActivity()).load(avatarUrl).error(R.drawable.ic_club_avatar_default).into(imgAvatar);

            UIUtils.loadPhotoToActionBar((ActionBarActivity) getActivity(), ImageHelper.getUserListAvatar(avatarUrl), mTarget);
        }

        TextView txtStatus = (TextView) view.findViewById(R.id.txtStatus);
        ClubWorkingHours todayWorkingHours = mClub.getTodayWorkingHours();
        if (todayWorkingHours != null && ClubWorkingHours.STATUS_OPENED.equalsIgnoreCase(todayWorkingHours.getStatus())) {
            txtStatus.setTextColor(getResources().getColor(R.color.green));
            txtStatus.setText(R.string.open);
        } else {
            txtStatus.setTextColor(getResources().getColor(R.color.red_light));
            txtStatus.setText(R.string.closed_display);
        }
    }

    private void onHolderClubInfoClicked() {
        Intent intent = new Intent(getActivity(), ClubInfoActivity.class);
        intent.putExtra(ClubInfoActivity.EXTRA_CLUB, JSONConverter.newClub(mClub).toString());
        intent.putExtra(ClubInfoActivity.EXTRA_TITLE, mClub.getTitle());
        startActivity(intent);
    }

    private void onYesterdayClicked() {
        Intent intent = new Intent(getActivity(), YesterdayUsersGridActivity.class);
        intent.putExtra(YesterdayUsersGridActivity.EXTRA_CLUB_ID, mClub.getId());
        startActivity(intent);
    }

    private void onDistanceClicked() {
        Location location = LocationCheckinHelper.getInstance().getCurrentLocation();
        if(location == null) {
            return;
        }

        double myLat = location.getLatitude();
        double myLong = location.getLongitude();
        double clubLat = mClub.getLat();
        double clubLong = mClub.getLon();

        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + myLat + "," + myLong + "&daddr=" + clubLat + "," + clubLong));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            L.i("" + e);
        }
    }

    private void setProgressViewState(boolean isLoading) {
        View view = getView();
        if(view == null) {
            return;
        }

        if(mIsLoading == isLoading) {
            return;
        }

        if(!mIsLoading) {
            setLoading(view, true);
        } else {
            setLoading(view, false);
        }
    }

    private void onCheckInBtnClicked(final View view) {
        if(!mCanCheckInHere) {
            String messageTemplate = getString(R.string.you_need_to_be_within_m_in_order_to_check_in);
            String dialogMessage = String.format(messageTemplate, SessionManager.getInstance().getCheckInMaxDistance());

            showMessageDialog(ClubFragment.this,
                    ACTION_ID_CAN_NOT_CHECK_IN,
                    getString(R.string.app_name),
                    dialogMessage,
                    getString(R.string.ok),
                    null
            );
            return;
        }

        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        if (LocationCheckinHelper.getInstance().isCheckInHere(mClub)) {
            showProgressDialog(getString(R.string.checking_out));

            LocationCheckinHelper.getInstance().checkOut(getActivity(), new CheckInOutCallbackInterface() {
                @Override
                public void onCheckInOutFinished(boolean isSuccess) {
                    if(!isSuccess) {
                        showToast(R.string.something_went_wrong_please_try_again);
                        return;
                    }

                    handleCheckInCheckOutResults(view, false);
                }
            });
        } else {
            showProgressDialog(getString(R.string.checking_in));

            if(!getSession().isCheckInDialogShown()) {
                showMessageDialog(ClubFragment.this,
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
        LocationCheckinHelper.getInstance().checkIn(getActivity(), mClub, new CheckInOutCallbackInterface() {
            @Override
            public void onCheckInOutFinished(boolean isSuccess) {
                if(!isSuccess) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    return;
                }

                handleCheckInCheckOutResults(view, true);
            }
        });
    }

    private void setCheckInTxtPaddings(View view) {
        view.setPadding(
                (int)getResources().getDimension(R.dimen.btn_check_in_left_right_padding),
                0,
                (int)getResources().getDimension(R.dimen.btn_check_in_left_right_padding),
                0
        );
    }

    private void handleCheckInCheckOutResults(View view, boolean isCheckedIn) {
        if(isDetached() || getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        hideProgressDialog();

        UiHelper.changeCheckInState(getActivity(), view, isCheckedIn);
        setCheckInTxtPaddings(view);

        loadCheckedInUsers();
    }

    private void setLoading(View view, boolean isLoading) {
        mIsLoading = isLoading;

        if(isLoading) {
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }

    private void showProgressDialog(String message) {
        mIsLoading = true;

        Fragment dialogFragment = ProgressDialog.newInstance(ClubFragment.this, getString(R.string.app_name), message);
        getChildFragmentManager().beginTransaction().add(dialogFragment, ProgressDialog.TAG).commitAllowingStateLoss();
    }

    private void hideProgressDialog() {
        mIsLoading = false;

        DialogFragment dialogFragment = (DialogFragment)getChildFragmentManager().findFragmentByTag(ProgressDialog.TAG);
        if(dialogFragment != null) {
            dialogFragment.dismissAllowingStateLoss();
        }
    }

    private void fillCheckedInUsers(@NotNull View view, @Nullable List<User> users) {
        TextView txtCheckInCount = (TextView) view.findViewById(R.id.txtCheckInCount);
        mProfileAdapter.updateData(users);

        if(users == null || users.isEmpty()) {
            view.findViewById(R.id.txtNoUsers).setVisibility(View.VISIBLE);
            txtCheckInCount.setText(0 + "\n" + getString(R.string.checked_in));

            checkCheckInState(view);
            return;
        }

        mClub.setActiveCheckIns(users.size());
        txtCheckInCount.setText(mClub.getActiveCheckIns() + "\n" + getString(R.string.checked_in));
        view.findViewById(R.id.txtNoUsers).setVisibility(View.GONE);

        boolean isCheckedInHere = false;
        String currentUserId = getSession().getUserDetails().get(SessionManager.KEY_ID);
        if(!LocationCheckinHelper.getInstance().isCheckIn()) {
            for(User user : users) {
                if(user != null && currentUserId.equalsIgnoreCase(user.getId())) {
                    LocationCheckinHelper.getInstance().setCurrentClub(mClub);

                    sendCheckedInOutBroadcast();

                    View txtCheckIn = view.findViewById(R.id.txtCheckIn);
                    UiHelper.changeCheckInState(getActivity(), txtCheckIn, true);
                    setCheckInTxtPaddings(txtCheckIn);

                    isCheckedInHere = true;

                    break;
                }
            }
        }

        if(!isCheckedInHere) {
            checkCheckInState(view);
        }
    }

    private void checkCheckInState(View view) {
        Club currentClub = LocationCheckinHelper.getInstance().getCurrentClub();
        if(currentClub != null && mClub.getId().equalsIgnoreCase(currentClub.getId())) {
            LocationCheckinHelper.getInstance().clearCheckedInClubInfo();

            sendCheckedInOutBroadcast();

            View txtCheckIn = view.findViewById(R.id.txtCheckIn);
            UiHelper.changeCheckInState(getActivity(), txtCheckIn, false);
            setCheckInTxtPaddings(txtCheckIn);
        }
    }

    private void sendCheckedInOutBroadcast() {
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_CHECK_IN_CHECK_OUT);
        getActivity().sendBroadcast(intent);
    }
}