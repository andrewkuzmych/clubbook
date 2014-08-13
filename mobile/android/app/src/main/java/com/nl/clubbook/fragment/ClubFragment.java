package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.adapter.ProfileAdapter;
import com.nl.clubbook.control.ExpandableHeightGridView;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.DataStore;
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
public class ClubFragment extends BaseFragment {

    private float initialX;
    private int position = 0;

    private ClubDto mClub;

    protected ImageLoader imageLoader;
    protected DisplayImageOptions options;
    protected ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();
    private String club_id;

    public ClubFragment(){}

    public ClubFragment(BaseFragment previousFragment, String club_id) {
        super(previousFragment);
        this.club_id = club_id;
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

    private void initImageLoader() {
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_list_image)
                .showImageForEmptyUri(R.drawable.default_list_image)
                .showImageOnFail(R.drawable.default_list_image)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        view.findViewById(R.id.btnCheckIn).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
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
                        public void onCheckInOutFinished(boolean isUserCheckin) {
                            // Do something when download finished
                            if (isUserCheckin) {
                                UiHelper.changeCheckinState(getActivity(), view, false);
                                loadData();
                            }
                        }
                    });
                }
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

        DataStore.retrievePlace(club_id, user.get(SessionManager.KEY_ID), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if(isDetached() || getActivity() == null) {
                    return;
                }

                setLoading(view, false);

                if (failed) {
                    //TODO
                    return;
                }

                mClub = (ClubDto) result;

                getActivity().setTitle("Club details"); //TODO

                TextView btnCheckIn = (TextView) view.findViewById(R.id.btnCheckIn);
                final ImageView imgClubCoverItem = (ImageView) view.findViewById(R.id.imgClubCoverItem);
                final ImageSwitcher clubCoverSwitcher = (ImageSwitcher) view.findViewById(R.id.clubCoverSwitcher);
                final TextView txtImageSlider = (TextView) view.findViewById(R.id.txtImageSlider);
                TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
                TextView txtDistance = (TextView) view.findViewById(R.id.txtDistance);
                TextView txtAddress = (TextView) view.findViewById(R.id.txtAddress);

                // if we checked in this this club set related style
                if (LocationCheckinHelper.isCheckinHere(mClub)) {
                    UiHelper.changeCheckinState(getActivity(), btnCheckIn, false);
                } else {
                    UiHelper.changeCheckinState(getActivity(), btnCheckIn, true);
                }
                // can we check in this club
                if (LocationCheckinHelper.canCheckinHere(mClub)) {
                    btnCheckIn.setEnabled(true);
                } else {
                    btnCheckIn.setEnabled(false);
                }

                // Load profiles
                initGridView(view, mClub.getUsers());

                txtTitle.setText(mClub.getTitle());
                txtAddress.setText(mClub.getAddress());
                txtDistance.setText(LocationCheckinHelper.formatDistance(getActivity().getApplicationContext(), mClub.getDistance()));

                if (mClub.getPhotos() != null && mClub.getPhotos().size() > 0) {
                    String image_url = ImageHelper.getClubImage(mClub.getPhotos().get(position));
                    imageLoader.displayImage(image_url, imgClubCoverItem, options, animateFirstListener);
                    txtImageSlider.setText(String.valueOf(position + 1) + "/" + String.valueOf(mClub.getPhotos().size()));

                    clubCoverSwitcher.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(mClub.getPhotos() == null || mClub.getPhotos().size() <= 1) {
                                return false;
                            }

                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    initialX = event.getX();
                                    break;
                                case MotionEvent.ACTION_UP:
                                case MotionEvent.ACTION_CANCEL:
                                    float finalX = event.getX();
                                    if (initialX > finalX) {
                                        clubCoverSwitcher.setInAnimation(getActivity(), R.anim.left_in);
                                        clubCoverSwitcher.setOutAnimation(getActivity(), R.anim.left_out);

                                        // next
                                        position++;
                                        if (position >= mClub.getPhotos().size()) {
                                            position = 0;
                                        }

                                        String image_url = ImageHelper.getClubImage(mClub.getPhotos().get(position));
                                        imageLoader.displayImage(image_url, imgClubCoverItem, options, animateFirstListener);
                                        txtImageSlider.setText(String.valueOf(position + 1) + "/" + String.valueOf(mClub.getPhotos().size()));

                                        clubCoverSwitcher.showNext();

                                    } else {
                                        clubCoverSwitcher.setInAnimation(getActivity(), R.anim.right_in);
                                        clubCoverSwitcher.setOutAnimation(getActivity(), R.anim.right_out);

                                        // prev
                                        if (position > 0) {
                                            position = position - 1;
                                        } else {
                                            position = mClub.getPhotos().size() - 1;
                                        }

                                        String image_url = ImageHelper.getClubImage(mClub.getPhotos().get(position));
                                        imageLoader.displayImage(image_url, imgClubCoverItem, options, animateFirstListener);
                                        txtImageSlider.setText(String.valueOf(position + 1) + "/" + String.valueOf(mClub.getPhotos().size()));

                                        clubCoverSwitcher.showPrevious();
                                    }
                                    break;
                            }
                            return true;
                        }
                    });
                }
            }
        });
    }

    private void setLoading(View view, boolean isLoading) {
        if(isLoading) {
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btnCheckIn).setVisibility(View.GONE);
            view.findViewById(R.id.scrollView).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            view.findViewById(R.id.btnCheckIn).setVisibility(View.VISIBLE);
            view.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
        }
    }

    private void initGridView(View view, List<UserDto> users) {
        if(users == null) {
            return;
        }

        ExpandableHeightGridView profileGridView = (ExpandableHeightGridView) view.findViewById(R.id.gridView);
        profileGridView.setExpanded(true);

        ProfileAdapter profileAdapter = new ProfileAdapter(getActivity(), users);
        profileGridView.setAdapter(profileAdapter);

        profileGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                View userId = view.findViewById(R.id.userId);

                openFragment(new ProfileFragment(ClubFragment.this, (String)userId.getTag()));
            }
        });
    }
}