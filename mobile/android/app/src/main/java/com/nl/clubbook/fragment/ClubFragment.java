package com.nl.clubbook.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
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
import com.nl.clubbook.adapter.ProfileAdapter;
import com.nl.clubbook.datasource.CheckInUserDto;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.ClubWorkingHoursDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.JSONConverter;
import com.nl.clubbook.fragment.dialog.MessageDialog;
import com.nl.clubbook.fragment.dialog.ProgressDialog;
import com.nl.clubbook.helper.*;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;
import com.nl.clubbook.utils.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Andrew on 6/8/2014.
 */
public class ClubFragment extends BaseInnerFragment implements View.OnClickListener, AdapterView.OnItemClickListener,
        ProgressDialog.OnDialogCanceledListener, MessageDialog.MessageDialogListener {

    public static final String TAG = ClubFragment.class.getSimpleName();

    private static final String ARG_CLUB_ID = "ARG_CLUB_ID";

    public static final int LOAD_MODE_INIT = 1111;
    public static final int LOAD_MODE_CHECK_IN = 9999;

    private ClubDto mClub;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener mAnimateFirstListener = new SimpleImageLoadingListener();
    private String mClubId;

    private boolean mIsLoading = false;
    private boolean mCanCheckInHere = false;

    public static Fragment newInstance(Fragment targetFragment, String clubId) {
        Fragment fragment = new ClubFragment();
        fragment.setTargetFragment(targetFragment, 0);

        Bundle args = new Bundle();
        args.putString(ARG_CLUB_ID, clubId);
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

        UIUtils.displayEmptyIconInActionBar((ActionBarActivity)getActivity());
        initActionBarTitle(getString(R.string.checked_in));
        handleArgs();
        initImageLoader();
        initView();
        loadData(LOAD_MODE_INIT);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            initActionBarTitle(getString(R.string.club_page));
            UIUtils.loadPhotoToActionBar((ActionBarActivity)getActivity(), ImageHelper.getUserListAvatar(mClub.getAvatar()));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtCheckIn:
                onCheckInBtnClicked(view);
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
        View viewUserId = view.findViewById(R.id.userId);

        if(!LocationCheckinHelper.getInstance().isCheckInHere(mClub)) {
            showToast(R.string.need_to_check_in_first);
            return;
        }

        String userId = (String)viewUserId.getTag();
        Fragment fragment = ProfileFragment.newInstance(ClubFragment.this, userId, mClub.getUsers(), ProfileFragment.OPEN_MODE_DEFAULT);
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
        dialogFragment.dismissAllowingStateLoss();
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

        mClubId = args.getString(ARG_CLUB_ID);
    }

    private void initImageLoader() {
        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_club_avatar_default)
                .showImageForEmptyUri(R.drawable.ic_club_avatar_default)
                .showImageOnFail(R.drawable.ic_club_avatar_default)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        view.findViewById(R.id.txtCheckIn).setOnClickListener(this);
        view.findViewById(R.id.holderClubInfo).setOnClickListener(this);
        view.findViewById(R.id.txtDistance).setOnClickListener(this);

        GridView gridUsers = (GridView) view.findViewById(R.id.gridUsers);
        gridUsers.setOnItemClickListener(ClubFragment.this);
    }

    public void onClubCheckedOut() {
        loadData(LOAD_MODE_CHECK_IN);
    }

    protected void loadData(final int mode) {
        final View view = getView();
        if(view == null) {
            return;
        }

        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        final HashMap<String, String> user = this.getSession().getUserDetails();

        setProgressViewState(mode, true);

        DataStore.retrievePlace(mClubId, user.get(SessionManager.KEY_ACCESS_TOCKEN), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                View view = getView();
                if(view == null || isDetached() || getActivity() == null) {
                    return;
                }

                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    hideProgressDialog();
                    return;
                }

                setProgressViewState(mode, false);

                mClub = (ClubDto) result;

                fillView(view);
            }
        });
    }

    private void fillView(View view) {
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

        // Load profiles
        initGridView(view, mClub.getUsers());

        // if we checked in this this club set related style
        boolean isCheckedInHere = LocationCheckinHelper.getInstance().isCheckInHere(mClub);
        if (isCheckedInHere) {
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

        txtClubName.setText(mClub.getTitle());
        txtCheckInCount.setText(mClub.getActiveCheckIns() + "\n" + getString(R.string.checked_in));
        txtFriendsCount.setText(mClub.getActiveFriendsCheckIns() + "\n" + getString(R.string.friends));
        txtDistance.setText(LocationCheckinHelper.formatDistance(getActivity().getApplicationContext(), mClub.getDistance()));

        ClubWorkingHoursDto workingHours = mClub.getTodayWorkingHours();
        if (workingHours != null) {
            if (ClubWorkingHoursDto.STATUS_OPENED.equalsIgnoreCase(workingHours.getStatus())) {
                String startTime = workingHours.getStartTime();
                String endTime = workingHours.getEndTime();

                txtOpenToday.append("  ");
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
            mImageLoader.displayImage(avatarUrl, imgAvatar, mOptions, mAnimateFirstListener);

            UIUtils.loadPhotoToActionBar((ActionBarActivity) getActivity(), ImageHelper.getUserListAvatar(avatarUrl));
        }

        TextView txtStatus = (TextView) view.findViewById(R.id.txtStatus);
        ClubWorkingHoursDto todayWorkingHours = mClub.getTodayWorkingHours();
        if (todayWorkingHours != null && ClubWorkingHoursDto.STATUS_OPENED.equalsIgnoreCase(todayWorkingHours.getStatus())) {
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

    private void setProgressViewState(int mode, boolean isLoading) {
        View view = getView();
        if(view == null) {
            return;
        }

        if(mIsLoading == isLoading) {
            return;
        }

        if(!mIsLoading) {
            if (mode == LOAD_MODE_CHECK_IN) {
                showProgressDialog(getString(R.string.checking_in));
            } else {
                setLoading(view, true);
            }
        } else {
            if (mode == LOAD_MODE_CHECK_IN) {
                hideProgressDialog();
            } else {
                setLoading(view, false);
            }
        }
    }

    private void onCheckInBtnClicked(final View view) {
        if(!mCanCheckInHere) {
            String messageTemplate = getString(R.string.you_need_to_be_within_m_in_order_to_check_in);
            String dialogMessage = String.format(messageTemplate, SessionManager.getInstance().getCheckInMaxDistance());

            DialogFragment messageDialog = MessageDialog.newInstance(
                    ClubFragment.this,
                    getString(R.string.app_name),
                    dialogMessage,
                    getString(R.string.ok),
                    null
            );

            getFragmentManager().beginTransaction().add(messageDialog, MessageDialog.TAG).commitAllowingStateLoss();
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
                public void onCheckInOutFinished(boolean isUserCheckOut) {
                    handleCheckInCheckOutResults(view, isUserCheckOut);
                }
            });
        } else {
            showProgressDialog(getString(R.string.checking_in));

            LocationCheckinHelper.getInstance().checkIn(getActivity(), mClub, new CheckInOutCallbackInterface() {
                @Override
                public void onCheckInOutFinished(boolean isUserCheckIn) {
                    handleCheckInCheckOutResults(view, isUserCheckIn);
                }
            });
        }
    }

    private void handleCheckInCheckOutResults(View view, boolean result) {
        if(isDetached() || getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        if (result) {
            UiHelper.changeCheckInState(getActivity(), view, false);
            loadData(LOAD_MODE_CHECK_IN);
        } else {
            setProgressViewState(LOAD_MODE_CHECK_IN, false);
        }
    }

    private void setLoading(View view, boolean isLoading) {
        mIsLoading = isLoading;

        if(isLoading) {
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            view.findViewById(R.id.holderScreen).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            view.findViewById(R.id.holderScreen).setVisibility(View.VISIBLE);
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

    private void initGridView(@NotNull View view, @Nullable List<CheckInUserDto> users) {
        if(users == null) {
            view.findViewById(R.id.holderCheckInExplanation).setVisibility(View.VISIBLE);
            return;
        }

        String currentUserId = getSession().getUserDetails().get(SessionManager.KEY_ID);
        if(!LocationCheckinHelper.getInstance().isCheckIn()) {
            for(CheckInUserDto user : users) {
                if(user != null && currentUserId.equalsIgnoreCase(user.getId())) {
                    LocationCheckinHelper.getInstance().setCurrentClub(mClub);

                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_CHECK_IN_CHECK_OUT);
                    getActivity().sendBroadcast(intent);
                }
            }
        }

        GridView gridUsers = (GridView) view.findViewById(R.id.gridUsers);
        ProfileAdapter profileAdapter = new ProfileAdapter(getActivity(), users, ProfileAdapter.MODE_GRID);
        gridUsers.setAdapter(profileAdapter);

        if(users.size() == 0) {
            view.findViewById(R.id.holderCheckInExplanation).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.holderCheckInExplanation).setVisibility(View.GONE);
        }
    }
}