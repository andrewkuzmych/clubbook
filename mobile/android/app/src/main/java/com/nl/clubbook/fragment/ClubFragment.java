package com.nl.clubbook.fragment;

import android.app.Activity;
import android.content.Intent;
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
import com.nl.clubbook.adapter.ProfileAdapter;
import com.nl.clubbook.datasource.CheckInUserDto;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.ClubWorkingHoursDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.JSONConverter;
import com.nl.clubbook.fragment.dialog.ProgressDialog;
import com.nl.clubbook.helper.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Andrew on 6/8/2014.
 */
public class ClubFragment extends BaseInnerFragment implements View.OnClickListener, AdapterView.OnItemClickListener,
        ProgressDialog.OnDialogCanceledListener {

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

        initActionBarTitle(getString(R.string.club_page));
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
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        View userId = view.findViewById(R.id.userId);
        Fragment fragment = ProfileFragment.newInstance(ClubFragment.this, (String)userId.getTag(), mClub.getUsers(), ProfileFragment.OPEN_MODE_DEFAULT);
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

        final HashMap<String, String> user = this.getSession().getUserDetails();

        setProgressViewState(mode, true);

        DataStore.retrievePlace(mClubId, user.get(SessionManager.KEY_ACCESS_TOCKEN), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if(isDetached() || getActivity() == null) {
                    return;
                }

                setProgressViewState(mode, false);

                if (failed) {
                    showNoInternetActivity();
                    return;
                }

                mClub = (ClubDto) result;

                fillView(view);
            }
        });
    }

    private void fillView(View view) {
        if(mClub == null) {
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
        if (LocationCheckinHelper.getInstance().isCheckInHere(mClub)) {
            UiHelper.changeCheckInState(getActivity(), txtCheckIn, true);
        } else {
            UiHelper.changeCheckInState(getActivity(), txtCheckIn, false);
        }
        // can we check in this club
        if (LocationCheckinHelper.getInstance().canCheckInHere(mClub)) {
            txtCheckIn.setEnabled(true);
        } else {
            txtCheckIn.setEnabled(false);
        }

        // Load profiles
        initGridView(view, mClub.getUsers());

        txtClubName.setText(mClub.getTitle());
        txtCheckInCount.setText(mClub.getActiveCheckIns() + "\n" + getString(R.string.checked_in));
        txtFriendsCount.setText(mClub.getActiveFriendsCheckIns() + "\n" + getString(R.string.friends));
        txtDistance.setText(LocationCheckinHelper.formatDistance(getActivity().getApplicationContext(), mClub.getDistance()));

        ClubWorkingHoursDto workingHours = mClub.getTodayWorkingHours();
        if(workingHours != null) {
            String startTime = workingHours.getStartTime();
            String endTime = workingHours.getEndTime();

            txtOpenToday.append("  ");
            txtOpenToday.append(startTime != null ? startTime + " - " : "");
            txtOpenToday.append(endTime != null ? endTime : "");
        }

        String avatarUrl = mClub.getAvatar();
        if(avatarUrl != null && avatarUrl.length() > 0) {
            mImageLoader.displayImage(avatarUrl, imgAvatar, mOptions, mAnimateFirstListener);
        }
    }

    private void onHolderClubInfoClicked() {
        Intent intent = new Intent(getActivity(), ClubInfoActivity.class);
        intent.putExtra(ClubInfoActivity.EXTRA_CLUB, JSONConverter.newClub(mClub).toString());
        intent.putExtra(ClubInfoActivity.EXTRA_TITLE, mClub.getTitle());
        startActivity(intent);
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

    private void initGridView(View view, List<CheckInUserDto> users) {
        if(users == null) {
            return;
        }

        String currentUserId = getSession().getUserDetails().get(SessionManager.KEY_ID);
        GridView gridUsers = (GridView) view.findViewById(R.id.gridUsers);
        ProfileAdapter profileAdapter = new ProfileAdapter(getActivity(), users, currentUserId, ProfileAdapter.MODE_GRID);
        gridUsers.setAdapter(profileAdapter);
    }
}