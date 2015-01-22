package com.nl.clubbook.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.nl.clubbook.R;
import com.nl.clubbook.ui.adapter.ClubPhotoPagerAdapter;

/**
 * Created by Volodymyr on 29.09.2014.
 */
public class ImagesGalleryActivity extends BaseActivity {

    public static final String EXTRA_PHOTOS_URLS = "EXTRA_PHOTOS_URLS";
    public static final String EXTRA_SELECTED_PHOTO = "EXTRA_SELECTED_PHOTO";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_image_view);

        setupToolBar();
        initActionBar();
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

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.selector_btn_home_black);
    }

    private void initView() {
        Intent intent = getIntent();
        String[] photoUrls = intent.getStringArrayExtra(EXTRA_PHOTOS_URLS);
        int selectedPhoto = intent.getIntExtra(EXTRA_SELECTED_PHOTO, 0);

        if(photoUrls != null) {
            ViewPager pagerImage = (ViewPager) findViewById(R.id.pagerPhoto);
            ClubPhotoPagerAdapter adapter = new ClubPhotoPagerAdapter(getSupportFragmentManager(), photoUrls);
            pagerImage.setAdapter(adapter);
            pagerImage.setCurrentItem(selectedPhoto, false);
        }
    }
}
