package com.nl.clubbook.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.nl.clubbook.R;
import com.nl.clubbook.adapter.UsersNearbyPagerAdapter;

/**
 * Created by Volodymyr on 18.12.2014.
 */
public class UsersNearbyFragment extends BaseFragment implements UsersNearbyGridFragment.OnGetDistanceListener,
        UsersNearbyGridFragment.OnRefreshListener, UsersNearbyGridFragment.OnGetFilterListener {

    public static final int[] DISTANCES = new int[] { 1, 2, 3, 4, 5, 10, 15, 20 };
    public static final int DEFAULT_DISTANCE = DISTANCES.length - 1;

    private ViewPager mViewPager;
    private SeekBar mSeekBar;
    private View mFilterHolder;
    private RadioButton mRbAll;
    private RadioButton mRbMale;
    private RadioButton mRbFemale;

    private int mFilterHolderHeight;
    private int mViewPagerHeight;
    private boolean mIsFilterHolderHidden = true;
    private int mCurrentProgress = DEFAULT_DISTANCE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_users_nearby, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initActionBarTitle(getString(R.string.users_nearby));
        initView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Activity activity = getActivity();
        if(!hidden && activity != null) {
            ActionBar actionBar = ((ActionBarActivity)activity).getSupportActionBar();
            actionBar.setTitle(R.string.users_nearby);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menuFilter) {
            onFilterClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public int onGetDistanceListener() {
        return DISTANCES[mCurrentProgress];
    }

    @Override
    public String onGetFilter() {
        if(mRbMale.isChecked()) {
            return Filter.MALE;
        } else if(mRbFemale.isChecked()) {
            return Filter.FEMALE;
        } else {
            return Filter.ALL;
        }
    }

    @Override
    public void onRefresh(boolean isRefreshing) {
        mSeekBar.setEnabled(!isRefreshing);
        mRbAll.setEnabled(!isRefreshing);
        mRbMale.setEnabled(!isRefreshing);
        mRbFemale.setEnabled(!isRefreshing);
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        // init ViewPager
        String[] titles = getResources().getStringArray(R.array.users_nearby_titles);
        final UsersNearbyPagerAdapter adapter = new UsersNearbyPagerAdapter(UsersNearbyFragment.this, getChildFragmentManager(), titles);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setAdapter(adapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);

        // display distance
        TextView txtDistance = (TextView) view.findViewById(R.id.distance_text);
        txtDistance.setText(DISTANCES[mCurrentProgress] + " " + getString(R.string.km));

        initSeekBar(view, txtDistance, adapter);
        initFilterHolder(view);
        initFilterRadioGroup(adapter);

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPagerHeight = mViewPager.getHeight();

                mViewPager.animate().yBy(-mFilterHolderHeight).setDuration(0).start();
                mViewPager.setLayoutParams(getViewPagerLayoutParams(true));
            }
        });
    }

    private void initFilterRadioGroup(@NonNull final UsersNearbyPagerAdapter adapter) {
        View view = getView();
        if(view == null) {
            return;
        }

        mRbAll = (RadioButton) view.findViewById(R.id.rbAll);
        mRbMale = (RadioButton) view.findViewById(R.id.rbMale);
        mRbFemale = (RadioButton) view.findViewById(R.id.rbFemale);

        mRbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mRbMale.setChecked(false);
                    mRbFemale.setChecked(false);
                }

                adapter.refreshFragments();
            }
        });

        mRbMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mRbAll.setChecked(false);
                    mRbFemale.setChecked(false);
                }

                adapter.refreshFragments();
            }
        });

        mRbFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRbMale.setChecked(false);
                    mRbAll.setChecked(false);
                }

                adapter.refreshFragments();
            }
        });
    }

    private void initSeekBar(@NonNull View view, @NonNull final TextView txtDistance, @NonNull final UsersNearbyPagerAdapter adapter) {
        mSeekBar = (SeekBar) view.findViewById(R.id.seekBarDistance);
        mSeekBar.setMax(DISTANCES.length - 1);
        mSeekBar.incrementProgressBy(1);
        mSeekBar.setProgress(mCurrentProgress);
        mSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        mCurrentProgress = progressValue;
                        txtDistance.setText(DISTANCES[mCurrentProgress] + " " + getString(R.string.km));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        adapter.refreshFragments();
                    }
                }
        );

        mCurrentProgress = mSeekBar.getProgress();
    }

    private void initFilterHolder(@NonNull View view) {
        mFilterHolder = view.findViewById(R.id.holderFilter);

        mFilterHolder.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mFilterHolderHeight = mFilterHolder.getMeasuredHeight();

        mFilterHolder.animate().yBy(-mFilterHolderHeight).setDuration(0).start();
    }

    private void onFilterClicked() {
        if(mFilterHolder == null) {
            return;
        }

        if(!mIsFilterHolderHidden) {
            ViewGroup.LayoutParams layoutParams = getViewPagerLayoutParams(true);
            mViewPager.setLayoutParams(layoutParams);

            mIsFilterHolderHidden = true;
            mFilterHolder.animate().yBy(-mFilterHolderHeight).setListener(null).start();
            mViewPager.animate().yBy(-mFilterHolderHeight).start();
        } else {
            mIsFilterHolderHidden = false;
            mFilterHolder.setVisibility(View.VISIBLE);

            mFilterHolder.animate().yBy(mFilterHolderHeight).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewGroup.LayoutParams layoutParams = getViewPagerLayoutParams(false);
                    mViewPager.setLayoutParams(layoutParams);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }).start();
            mViewPager.animate().yBy(mFilterHolderHeight).start();
        }
    }

    private ViewGroup.LayoutParams getViewPagerLayoutParams(boolean isFilterHidden) {
        if(!isFilterHidden) {
            return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mViewPagerHeight);
        } else {
            return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mViewPagerHeight + mFilterHolderHeight);
        }
    }

    public interface Filter {
        public static final String ALL = "";
        public static final String MALE = "male";
        public static final String FEMALE = "female";
    }
}
