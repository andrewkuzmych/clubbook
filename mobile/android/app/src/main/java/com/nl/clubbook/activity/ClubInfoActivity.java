package com.nl.clubbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.JSONConverter;
import com.nl.clubbook.fragment.ClubInfoFragment;
import com.nl.clubbook.utils.L;

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

        initActionBar(screenTitle);
        initClubInfoFragment(jsonClub);
    }

    private void initClubInfoFragment(String jsonClub) {
        Fragment fragment = ClubInfoFragment.newInstance(jsonClub);
        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.replace(R.id.fragmentContainer, fragment).commit();
    }
}
