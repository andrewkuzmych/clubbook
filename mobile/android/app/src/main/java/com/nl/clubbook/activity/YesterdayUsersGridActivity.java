package com.nl.clubbook.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.ProfileAdapter;

/**
 * Created by Volodymyr on 31.10.2014.
 */
public class YesterdayUsersGridActivity extends BaseActivity {

    public static final String EXTRA_CLUB_ID = "";

    private ProfileAdapter mProfileAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_yesterday_users_grid);

        initActionBar(R.string.club_profile);
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

    }
}
