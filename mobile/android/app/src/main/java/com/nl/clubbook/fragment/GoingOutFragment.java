package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.nl.clubbook.R;
import com.nl.clubbook.adapter.GoingOutPagerAdapter;

/**
 * Created by Volodymyr on 18.12.2014.
 */
public class GoingOutFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_going_out, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initActionBarTitle(getString(R.string.going_out));
        initView();
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        GoingOutPagerAdapter adapter = new GoingOutPagerAdapter(getChildFragmentManager(), getResources().getStringArray(R.array.going_ouy_titles));
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(GoingOutPagerAdapter.ITEMS_COUNT);
        viewPager.setAdapter(adapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
    }
}
