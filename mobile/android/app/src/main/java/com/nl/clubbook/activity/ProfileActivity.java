package com.nl.clubbook.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.User;
import com.nl.clubbook.fragment.ProfileFragment;

/**
 * Created by Volodymyr on 06.11.2014.
 */
public class ProfileActivity extends BaseActivity {

    public static final String EXTRA_USER = "EXTRA_USER_ID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_profile);

        initActionBar(R.string.club_profile);
        initFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fManager = getSupportFragmentManager();
        if(fManager.getBackStackEntryCount() > 0) {
            fManager.popBackStack();
        } else {
            finish();
        }
    }

    private void initFragment() {
        User user = getIntent().getParcelableExtra(EXTRA_USER);
        if(user == null) {
            return;
        }

        Fragment fragment = ProfileFragment.newInstance(user, ProfileFragment.OPEN_MODE_DEFAULT);
        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.replace(R.id.fragmentContainer, fragment);
        fTransaction.commit();
    }
}
