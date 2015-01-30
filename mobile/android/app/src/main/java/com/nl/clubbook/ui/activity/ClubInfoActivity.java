package com.nl.clubbook.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.nl.clubbook.R;
import com.nl.clubbook.model.data.JSONConverter;
import com.nl.clubbook.model.data.Place;
import com.nl.clubbook.ui.adapter.ClubInfoPagerAdapter;

/**
 * Created by Volodymyr on 15.08.2014.
 */
public class ClubInfoActivity extends BaseActivity {

    public static final String EXTRA_CLUB = "EXTRA_CLUB";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_club_info);

        setupToolBar();
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

    private void initView() {
        Intent intent = getIntent();
        String screenTitle = intent.getStringExtra(EXTRA_TITLE);
        String jsonClub = intent.getStringExtra(EXTRA_CLUB);

        Place place = JSONConverter.newPlace(jsonClub);
        if(place == null) {
            return;
        }

        initActionBar(screenTitle);

        //init ViewPager
        String[] titles = getResources().getStringArray(R.array.club_info_titles);
        ClubInfoPagerAdapter mClubInfoPagerAdapter = new ClubInfoPagerAdapter(getSupportFragmentManager(), titles, place);
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(ClubInfoPagerAdapter.FRAGMENT_COUNT);
        viewPager.setAdapter(mClubInfoPagerAdapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
    }
}
