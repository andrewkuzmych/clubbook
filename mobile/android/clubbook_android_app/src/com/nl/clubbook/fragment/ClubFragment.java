package com.nl.clubbook.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.adapter.ProfileAdapter;
import com.nl.clubbook.control.ExpandableHeightGridView;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.HashMap;

/**
 * Created by Andrew on 6/8/2014.
 */
public class ClubFragment extends BaseFragment {

    private ImageSwitcher clubCoverSwitcher;
    private ImageView clubCoverItem;
    private float initialX;
    private int position = 0;

    private Context mContext;
    TextView image_slider, title_text, address_text, distance_text;
    private ExpandableHeightGridView profileGridView;
    private ProfileAdapter profileAdapter;
    private Button checkin;
    private ClubDto club;

    protected ImageLoader imageLoader;
    protected DisplayImageOptions options;
    protected ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();
    private String club_id;

    public ClubFragment(BaseFragment previousFragment, String club_id) {
        super(previousFragment);
        this.club_id = club_id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_list_image)
                .showImageForEmptyUri(R.drawable.default_list_image)
                .showImageOnFail(R.drawable.default_list_image)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        View rootView = inflater.inflate(R.layout.fragment_club, container, false);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TITILLIUMWEB-REGULAR.TTF");
        Typeface typeface_bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TITILLIUMWEB-BOLD.TTF");

        title_text = (TextView) rootView.findViewById(R.id.title);
        title_text.setTypeface(typeface_bold);
        image_slider = (TextView) rootView.findViewById(R.id.image_slider);
        image_slider.setTypeface(typeface_bold);
        address_text = (TextView) rootView.findViewById(R.id.address);
        address_text.setTypeface(typeface);
        distance_text = (TextView) rootView.findViewById(R.id.distancekm);
        distance_text.setTypeface(typeface);
        checkin = (Button) rootView.findViewById(R.id.checkin);
        checkin.setTypeface(typeface_bold);
        checkin.getBackground().setAlpha(128);

        profileGridView = (ExpandableHeightGridView) rootView.findViewById(R.id.gridView);
        distance_text = (TextView) rootView.findViewById(R.id.distancekm);
        mContext = getActivity();

        // init club images
        clubCoverSwitcher = (ImageSwitcher) rootView.findViewById(R.id.clubCoverSwitcher);
        clubCoverItem = (ImageView) rootView.findViewById(R.id.clubCoverItem);

        loadData();

        return rootView;
    }

    protected void loadData() {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        final BaseFragment thisInstance = this;
        final HashMap<String, String> user = this.getSession().getUserDetails();

        ((BaseActivity) getActivity()).showProgress("Loading...");
        DataStore.retrievePlace(club_id, user.get(SessionManager.KEY_ID), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    ((BaseActivity) getActivity()).hideProgress(false);
                    return;
                }

                ((BaseActivity) getActivity()).hideProgress(true);

                club = (ClubDto) result;

                getActivity().setTitle("Club details");

                // if we checked in this this club set related style
                if (LocationCheckinHelper.isCheckinHere(club)) {
                    UiHelper.changeCheckinState(getActivity(), checkin, false);
                } else {
                    UiHelper.changeCheckinState(getActivity(), checkin, true);
                }
                // can we check in this club
                if (LocationCheckinHelper.canCheckinHere(club)) {
                    checkin.setEnabled(true);
                } else {
                    checkin.setEnabled(false);
                }

                setHandlers();
                // Load profiles
                profileGridView.setExpanded(true);
                profileAdapter = new ProfileAdapter(mContext, R.layout.profile_item, club.getUsers());
                profileGridView.setAdapter(profileAdapter);

                profileGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {

                        String user_id = ((TextView) v.findViewById(R.id.user_id)).getText().toString();
                        openFragment(new ProfileFragment(thisInstance, user_id));
                    }

                });

                title_text.setText(club.getTitle());
                address_text.setText(club.getAddress());
                distance_text.setText(LocationCheckinHelper.formatDistance(getActivity().getApplicationContext(), club.getDistance()));

                if (club.getPhotos().size() > 0) {
                    String image_url = ImageHelper.generateUrl(club.getPhotos().get(position), "c_fit,w_700");
                    imageLoader.displayImage(image_url, clubCoverItem, options, animateFirstListener);
                    image_slider.setText(String.valueOf(position + 1) + "/" + String.valueOf(club.getPhotos().size()));

                    clubCoverSwitcher.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    initialX = event.getX();
                                    break;
                                case MotionEvent.ACTION_UP:
                                case MotionEvent.ACTION_CANCEL:
                                    float finalX = event.getX();
                                    if (initialX > finalX) {
                                        clubCoverSwitcher.setInAnimation(mContext, R.anim.left_in);
                                        clubCoverSwitcher.setOutAnimation(mContext, R.anim.left_out);
                                        // next
                                        position++;
                                        if (position >= club.getPhotos().size())
                                            position = 0;

                                        String image_url = ImageHelper.generateUrl(club.getPhotos().get(position), "c_fit,w_700");
                                        imageLoader.displayImage(image_url, clubCoverItem, options, animateFirstListener);
                                        image_slider.setText(String.valueOf(position + 1) + "/" + String.valueOf(club.getPhotos().size()));

                                        clubCoverSwitcher.showNext();

                                    } else {
                                        clubCoverSwitcher.setInAnimation(mContext, R.anim.right_in);
                                        clubCoverSwitcher.setOutAnimation(mContext, R.anim.right_out);
                                        // prev
                                        if (position > 0)
                                            position = position - 1;
                                        else
                                            position = club.getPhotos().size() - 1;

                                        String image_url = ImageHelper.generateUrl(club.getPhotos().get(position), "c_fit,w_700");
                                        imageLoader.displayImage(image_url, clubCoverItem, options, animateFirstListener);
                                        image_slider.setText(String.valueOf(position + 1) + "/" + String.valueOf(club.getPhotos().size()));

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

    private void setHandlers() {
        checkin.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                if (LocationCheckinHelper.isCheckinHere(club)) {
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
                    LocationCheckinHelper.checkin(getActivity(), club, new CheckInOutCallbackInterface() {
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
}