package com.nl.clubbook.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.nl.clubbook.R;
import com.nl.clubbook.ui.adapter.GoingOutPagerAdapter;
import com.nl.clubbook.ui.view.DisableSwipingViewPager;

/**
 * Created by Volodymyr on 18.12.2014.
 */
public class GoingOutFragment extends BaseFragment implements PlacesFragment.OnGetSearchQueryListener {

    private DisableSwipingViewPager mViewPager;
    private GoingOutPagerAdapter mAdapter;
    private MenuItem mMenuMessagesCount;
    private String mSearchQuery = "";

    private boolean mInitSetupQuery = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_going_out, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendScreenStatistic(R.string.main_screen_android);

        initActionBarTitle(getString(R.string.going_out));
        initView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        mMenuMessagesCount = menu.findItem(R.id.badgeMessages);

        initSearchView(menu);
    }

    @Override
    public String getSearchQuery() {
        return mSearchQuery;
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mAdapter = new GoingOutPagerAdapter(getChildFragmentManager(), GoingOutFragment.this, getResources().getStringArray(R.array.going_ouy_titles));
        mViewPager = (DisableSwipingViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(GoingOutPagerAdapter.ITEMS_COUNT);
        mViewPager.setAdapter(mAdapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);
    }

    private void initSearchView(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(mInitSetupQuery && s.isEmpty()) {
                    mInitSetupQuery = false;
                    return false;
                }

                mSearchQuery = s;
                mAdapter.searchPlaces(mSearchQuery);

                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                mViewPager.setCurrentItem(GoingOutPagerAdapter.INDEX_ALL, true);
                mMenuMessagesCount.setVisible(false);
                mViewPager.setSwipeEnabled(false);

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                mMenuMessagesCount.setVisible(true);
                mViewPager.setSwipeEnabled(true);

                mSearchQuery = "";
                mInitSetupQuery = true;

                mAdapter.refreshAllPalcesFragment();

                return true;
            }
        });
    }
}
