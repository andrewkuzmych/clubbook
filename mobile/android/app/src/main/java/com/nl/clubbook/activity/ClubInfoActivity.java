package com.nl.clubbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.ClubInfoPagerAdapter;
import com.nl.clubbook.utils.UIUtils;

/**
 * Created by Volodymyr on 15.08.2014.
 */
public class ClubInfoActivity extends BaseActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    public static final String EXTRA_CLUB = "EXTRA_CLUB";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";

    private ViewPager mViewPager;
    private TabHost mTabHost;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_club_info);

        UIUtils.displayEmptyIconInActionBar(this);
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onPageSelected(int i) {
        mTabHost.setCurrentTab(i);
    }

    @Override
    public void onTabChanged(String tabId) {
        String info = getString(R.string.info);
        mViewPager.setCurrentItem(info.equalsIgnoreCase(tabId) ? ClubInfoPagerAdapter.INDEX_CLUB_INFO :
                ClubInfoPagerAdapter.INDEX_WALL);
    }

    private void initView() {
        Intent intent = getIntent();
        String screenTitle = intent.getStringExtra(EXTRA_TITLE);
        String jsonClub = intent.getStringExtra(EXTRA_CLUB);

        initActionBar(screenTitle);

        //init TabHost
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabHost.addTab(newTabSpec(mTabHost, getString(R.string.info)));
        mTabHost.addTab(newTabSpec(mTabHost, getString(R.string.wall)));

        mTabHost.setOnTabChangedListener(this);

        //init ViewPager
        ClubInfoPagerAdapter mClubInfoPagerAdapter = new ClubInfoPagerAdapter(getSupportFragmentManager(),jsonClub);
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mViewPager.setAdapter(mClubInfoPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    private TabHost.TabSpec newTabSpec(TabHost tabHost, String tabIndicator) {
        View tabIndicatorView = LayoutInflater.from(ClubInfoActivity.this).inflate(R.layout.apptheme_tab_indicator_holo, tabHost.getTabWidget(), false);
        TextView title = (TextView) tabIndicatorView.findViewById(android.R.id.title);
        title.setText(tabIndicator);

        return tabHost.newTabSpec(tabIndicator).setContent(android.R.id.tabcontent).setIndicator(tabIndicatorView);
    }
}
