package com.nl.clubbook.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
public class SelectedClubFragment extends BaseFragment {

    private static final int SWIPE_MIN_DISTANCE = 20;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private ViewFlipper mViewFlipper;
    private Animation.AnimationListener mAnimationListener;
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

    public SelectedClubFragment(BaseFragment prvoiusFragment, String club_id) {
        super(prvoiusFragment);
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

        View rootView = inflater.inflate(R.layout.fragment_selected_club, container, false);

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
        mViewFlipper = (ViewFlipper) rootView.findViewById(R.id.view_flipper);
        profileGridView = (ExpandableHeightGridView) rootView.findViewById(R.id.gridView);
        distance_text = (TextView) rootView.findViewById(R.id.distancekm);
        mContext = getActivity();

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        try {

                            // right to left swipe
                            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
                                mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
                                // controlling animation
                                mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);
                                mViewFlipper.showNext();
                            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
                                mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_out));
                                // controlling animation
                                mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);
                                mViewFlipper.showPrevious();
                            }

                            image_slider.setText(String.valueOf(mViewFlipper.getDisplayedChild() + 1) + "/" + String.valueOf(mViewFlipper.getChildCount()));

                            return true;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                }
        );

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        loadData();

        return rootView;
    }

    protected void loadData() {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        final BaseFragment thisInstance = this;
        final SessionManager session = new SessionManager(getActivity());
        final HashMap<String, String> user = session.getUserDetails();

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

                getActivity().setTitle(club.getTitle());

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
                        String user_title = ((TextView) v.findViewById(R.id.text)).getText().toString();
                        ChatFragment fragment = new ChatFragment(thisInstance, user_id, user_title);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();

                        mFragmentTransaction.addToBackStack(null);
                        mFragmentTransaction.replace(R.id.frame_container, fragment).commit();
                    }

                });

                mViewFlipper.removeAllViews();
                for (int i = 0; i < club.getPhotos().size(); i++) {
                    View clubPhoto = inflater.inflate(R.layout.club_photos, null);
                    ImageView image = (ImageView) clubPhoto.findViewById(R.id.photo);
                    mViewFlipper.addView(clubPhoto);
                    String image_url = ImageHelper.GenarateUrl(club.getPhotos().get(i), "c_fit,w_700");
                    imageLoader.displayImage(image_url, image, options, animateFirstListener);
                }

                image_slider.setText(String.valueOf(mViewFlipper.getDisplayedChild() + 1) + "/" + String.valueOf(mViewFlipper.getChildCount()));
                title_text.setText(club.getTitle());
                address_text.setText(club.getAddress());
                distance_text.setText(LocationCheckinHelper.calculateDistance(getActivity().getApplicationContext(), club.getDistance()));
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
                        public void onCheckInOutFinished(boolean result) {
                            // Do something when download finished
                            if (result) {
                                UiHelper.changeCheckinState(getActivity(), view, false);
                                loadData();
                            }
                        }
                    });
                }
            }
        });

        //animation listener
        mAnimationListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                //animation started event
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                //TODO animation stopped event
            }
        };
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
        ((MainActivity) getActivity()).setDefoltTitle();
        if (!((MainActivity) getActivity()).getDrawerToggle().isDrawerIndicatorEnabled()) {
            ((MainActivity) getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(true);
            ((MainActivity) getActivity()).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
}