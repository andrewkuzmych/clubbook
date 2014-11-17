package com.nl.clubbook.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.nl.clubbook.R;
import com.nl.clubbook.fragment.ProfilePageHolderFragment;
import com.nl.clubbook.helper.SingleUsersHolder;

/**
 * Created by Volodymyr on 06.11.2014.
 */
public class ProfileActivity extends BaseActivity {

    public static final String EXTRA_POSITION = "EXTRA_POSITION";

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
        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);

        Fragment fragment = ProfilePageHolderFragment.newInstance(null, SingleUsersHolder.getInstance().getUsers(), position);
        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.replace(R.id.fragmentContainer, fragment);
        fTransaction.commit();
    }
}
