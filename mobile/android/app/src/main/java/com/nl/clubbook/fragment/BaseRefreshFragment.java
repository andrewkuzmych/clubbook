package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.nl.clubbook.R;
import com.nl.clubbook.utils.NetworkUtils;

/**
 * Created by Volodymyr on 14.08.2014.
 */
public abstract class BaseRefreshFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final int DEFAULT_CLUBS_COUNT = 20;
    public static final int DEFAULT_CLUBS_SKIP = 0;

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected View mFooterProgress;
    protected View mProgressBar;

    protected int mSkipNumber = DEFAULT_CLUBS_SKIP;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if(view == null) {
            return;
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        if(mSwipeRefreshLayout == null) {
            throw new IllegalArgumentException("Fragment which extends BaseRefreshFragment should contain SwipeRefreshLayout with id 'swipeRefreshLayout'");
        }

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(R.color.holo_blue_bright,
                R.color.holo_green_light,
                R.color.holo_orange_light,
                R.color.holo_red_light);

        mProgressBar = view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        mFooterProgress = LayoutInflater.from(getActivity()).inflate(R.layout.view_footer_progress, null);
        mFooterProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRefresh() {
        if(!NetworkUtils.isOn(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();

            if(mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            return;
        }

        loadData();
    }

    protected abstract void loadData();

    protected void setProgressViewsState(boolean isSwipeToRefreshRefreshed, int footerProgressBarVisibility, int progressBarVisibility) {
        mSwipeRefreshLayout.setRefreshing(isSwipeToRefreshRefreshed);
        mFooterProgress.setVisibility(footerProgressBarVisibility);
        mProgressBar.setVisibility(progressBarVisibility);
    }
}
