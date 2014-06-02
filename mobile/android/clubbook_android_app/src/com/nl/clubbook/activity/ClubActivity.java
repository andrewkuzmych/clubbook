package com.nl.clubbook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.adapter.ProfileAdapter;
import com.nl.clubbook.control.ExpandableHeightGridView;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.ProfileItem;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.LocationHelper;

import java.util.ArrayList;

/**
 * Created by Andrew on 5/29/2014.
 */
public class ClubActivity extends BaseActivity {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private ViewFlipper mViewFlipper;
    private Animation.AnimationListener mAnimationListener;
    private Context mContext;
    TextView image_slider, title_text, address_text, distance_text;
    private ExpandableHeightGridView profileGridView;
    private ProfileAdapter profileAdapter;


    @SuppressWarnings("deprecation")
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club);
        mContext = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();

        Intent in = getIntent();
        String club_id = in.getStringExtra("club_id");
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/TITILLIUMWEB-REGULAR.TTF");
        Typeface typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/TITILLIUMWEB-BOLD.TTF");

        title_text = (TextView)findViewById(R.id.title);
        title_text.setTypeface(typeface_bold);
        image_slider = (TextView)findViewById(R.id.image_slider);
        image_slider.setTypeface(typeface_bold);
        address_text = (TextView)findViewById(R.id.address);
        address_text.setTypeface(typeface);
        distance_text = (TextView)findViewById(R.id.distancekm);
        distance_text.setTypeface(typeface);

        mViewFlipper = (ViewFlipper)findViewById(R.id.view_flipper);

        mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });

        final LayoutInflater inflater = LayoutInflater.from(ClubActivity.this);

        showProgress("Loading...");
        DataStore.retrievePlace(club_id, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    hideProgress(false);
                    return;
                }
                hideProgress(true);

                // Load profiles
                profileGridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
                profileGridView.setExpanded(true);

                profileAdapter = new ProfileAdapter(mContext, R.layout.profile_item, getData());
                profileGridView.setAdapter(profileAdapter);

                profileGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        Toast.makeText(ClubActivity.this, position + "#Selected",
                                Toast.LENGTH_SHORT).show();
                    }

                });


                ClubDto club = (ClubDto) result;

                for (int i = 0; i < club.getPhotos().size(); i++)
                {
                    View clubPhoto = inflater.inflate(R.layout.club_photos,null);
                    ImageView image = (ImageView)clubPhoto.findViewById(R.id.photo);
                    mViewFlipper.addView(clubPhoto);
                    String image_url = ImageHelper.GenarateUrl(club.getPhotos().get(i), "c_fit,w_700");
                    imageLoader.displayImage(image_url, image, options, animateFirstListener);
                }

                image_slider.setText(String.valueOf(mViewFlipper.getDisplayedChild() + 1) + "/" + String.valueOf(mViewFlipper.getChildCount()));
                title_text.setText(club.getTitle());
                address_text.setText(club.getAddress());
                distance_text = (TextView)findViewById(R.id.distancekm);
                distance_text.setText(LocationHelper.calculateDistance(getApplicationContext(), club.getDistance()));



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

    private ArrayList<ProfileItem> getData() {
        final ArrayList<ProfileItem> imageItems = new ArrayList<ProfileItem>();
        // retrieve String drawable array
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                    imgs.getResourceId(i, -1));
            imageItems.add(new ProfileItem(bitmap, "Image#" + i));
        }

        return imageItems;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
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
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,R.anim.right_out));
                    // controlling animation
                    mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);
                    mViewFlipper.showPrevious();
                }

                image_slider.setText(String.valueOf(mViewFlipper.getDisplayedChild() + 1) + "/" + String.valueOf(mViewFlipper.getChildCount()));
                return true;


            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }
}