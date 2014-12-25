package com.nl.clubbook.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.nl.clubbook.R;
import com.nl.clubbook.adapter.UsersNearbyPagerAdapter;

/**
 * Created by Volodymyr on 18.12.2014.
 */
public class UsersNearbyFragment extends BaseFragment implements UsersNearbyGridFragment.OnGetDistanceListener,
        UsersNearbyGridFragment.OnSetSeekBarEnablingListener {

    public static final int[] DISTANCES = new int[] { 1, 2, 3, 4, 5, 10, 15, 20 };
    public static final int DEFAULT_DISTANCE = DISTANCES.length - 1;

    private SeekBar mSeekBar;
    private int mCurrentProgress = DEFAULT_DISTANCE;

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
    public int getDistanceListener() {
        return DISTANCES[mCurrentProgress];
    }

    @Override
    public void setSeekBarEnabling(boolean isEnabled) {
        mSeekBar.setEnabled(isEnabled);
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        String[] titles = getResources().getStringArray(R.array.users_nearby_titles);
        final UsersNearbyPagerAdapter adapter = new UsersNearbyPagerAdapter(UsersNearbyFragment.this, getChildFragmentManager(), titles);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);

        final TextView txtDistance = (TextView) view.findViewById(R.id.distance_text);
        txtDistance.setText(DISTANCES[mCurrentProgress] + " " + getString(R.string.km));

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
}
