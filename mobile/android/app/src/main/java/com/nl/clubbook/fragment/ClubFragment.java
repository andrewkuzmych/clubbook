package com.nl.clubbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
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
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.ClubWorkingHoursDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.JSONConverter;
import com.nl.clubbook.datasource.UserDto;
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
public class ClubFragment extends BaseFragment implements View.OnClickListener {

    private ClubDto mClub;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener mAnimateFirstListener = new SimpleImageLoadingListener();
    private String mClubId;

    public ClubFragment(){}

    public ClubFragment(BaseFragment previousFragment, String clubId) {
        super(previousFragment);
        this.mClubId = clubId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_club, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initImageLoader();
        initView();
        loadData();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (((MainActivity) getActivity()).getDrawerToggle().isDrawerIndicatorEnabled()) {
            ((MainActivity) getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(false);
            ((MainActivity) getActivity()).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void backButtonWasPressed() {
        ((MainActivity) getActivity()).setDefaultTitle();
        if (!((MainActivity) getActivity()).getDrawerToggle().isDrawerIndicatorEnabled()) {
            ((MainActivity) getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(true);
            ((MainActivity) getActivity()).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtCheckedIn:
                onCheckInBtnClicked(view);
                break;
            case R.id.holderClubInfo:
                onHolderClubInfoClicked();
                break;
        }
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
        gridUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View userId = view.findViewById(R.id.userId);

                openFragment(new ProfileFragment(ClubFragment.this, (String)userId.getTag(), mClub.getUsers()));
            }
        });
    }

    protected void loadData() {
        final View view = getView();
        if(view == null) {
            return;
        }

        final HashMap<String, String> user = this.getSession().getUserDetails();

        setLoading(view, true);

        DataStore.retrievePlace(mClubId, user.get(SessionManager.KEY_ID), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if(isDetached() || getActivity() == null) {
                    return;
                }

                setLoading(view, false);

                if (failed) {
                    showNoInternetActivity();
                    return;
                }

                mClub = (ClubDto) result;

                getActivity().setTitle("Club details"); //TODO

                fillView(view);
            }
        });
    }

    private void fillView(View view) {
        TextView txtCheckIn = (TextView) view.findViewById(R.id.txtCheckIn);
        TextView txtClubName = (TextView) view.findViewById(R.id.txtClubName);
        TextView txtOpenToday = (TextView) view.findViewById(R.id.txtOpenToday);
        TextView txtCheckInCount = (TextView) view.findViewById(R.id.txtCheckInCount);
        TextView txtDistance = (TextView) view.findViewById(R.id.txtDistance);
        TextView txtFriendsCount = (TextView) view.findViewById(R.id.txtFriendsCount);
        ImageView imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);

        // if we checked in this this club set related style
        if (LocationCheckinHelper.isCheckinHere(mClub)) {
            UiHelper.changeCheckinState(getActivity(), txtCheckIn, false);
        } else {
            UiHelper.changeCheckinState(getActivity(), txtCheckIn, true);
        }
        // can we check in this club
        if (LocationCheckinHelper.canCheckinHere(mClub)) {
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

    private void onCheckInBtnClicked(final View view) {
        if (LocationCheckinHelper.isCheckinHere(mClub)) {
            LocationCheckinHelper.checkout(getActivity(), new CheckInOutCallbackInterface() {
                @Override
                public void onCheckInOutFinished(boolean result) {
                    // Do something when download finished
                    if (result) {
                        UiHelper.changeCheckinState(getActivity(), view, true);
                        loadData();
                    }
                }
            });
        } else {
            LocationCheckinHelper.checkin(getActivity(), mClub, new CheckInOutCallbackInterface() {
                @Override
                public void onCheckInOutFinished(boolean isUserCheckIn) {
                    // Do something when download finished
                    if (isUserCheckIn) {
                        UiHelper.changeCheckinState(getActivity(), view, false);
                        loadData();
                    }
                }
            });
        }
    }

    private void setLoading(View view, boolean isLoading) {
        if(isLoading) {
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            view.findViewById(R.id.holderScreen).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            view.findViewById(R.id.holderScreen).setVisibility(View.VISIBLE);
        }
    }

    private void initGridView(View view, List<UserDto> users) {
        if(users == null) {
            return;
        }

        GridView gridUsers = (GridView) view.findViewById(R.id.gridUsers);
        ProfileAdapter profileAdapter = new ProfileAdapter(getActivity(), users);
        gridUsers.setAdapter(profileAdapter);
    }
}