package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.nl.clubbook.R;

/**
 * Created by Volodymyr on 14.08.2014.
 */
public abstract class BaseRefreshFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    protected SwipeRefreshLayout mSwipeRefreshLayout;

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
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    protected abstract void loadData();
}
