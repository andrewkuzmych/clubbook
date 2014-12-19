package com.nl.clubbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.TabHost;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.ClubInfoPagerAdapter;
import com.nl.clubbook.utils.UIUtils;

/**
 * Created by Volodymyr on 15.08.2014.
 */
public class ClubInfoActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    public static final String EXTRA_CLUB = "EXTRA_CLUB";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";

    private ViewPager mViewPager;
    private TabHost mTabHost;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_club_info);

        setUpToolBar();
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
//        mTabHost.setCurrentTab(i);
    }

    private void initView() {
        Intent intent = getIntent(); //TODO
        String screenTitle = intent.getStringExtra(EXTRA_TITLE);
        String jsonClub = intent.getStringExtra(EXTRA_CLUB);

        initActionBar(screenTitle);

        //init ViewPager
        ClubInfoPagerAdapter mClubInfoPagerAdapter = new ClubInfoPagerAdapter(getSupportFragmentManager(),jsonClub);
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mViewPager.setAdapter(mClubInfoPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
    }
}
