package com.nl.clubbook.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nl.clubbook.R;
import com.nl.clubbook.model.ClubbookPreferences;
import com.nl.clubbook.ui.adapter.ProfileAdapter;
import com.nl.clubbook.model.httpclient.HttpClientManager;
import com.nl.clubbook.model.data.User;
import com.nl.clubbook.helper.SingleUsersHolder;
import com.nl.clubbook.utils.NetworkUtils;

import java.util.List;

/**
 * Created by Volodymyr on 31.10.2014.
 */
public class YesterdayUsersGridActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    public static final String EXTRA_CLUB_ID = "";

    private ProfileAdapter mProfileAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_yesterday_users_grid);

        sendScreenStatistic(R.string.yesterday_checked_in_users_android);

        setupToolBar();
        initActionBar(R.string.club_profile);
        loadCheckedInUsers();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SingleUsersHolder.getInstance().setUsers(mProfileAdapter.getUsers());

        Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_POSITION, position);
        startActivity(intent);
    }

    protected void loadCheckedInUsers() {
        String clubId = getIntent().getStringExtra(EXTRA_CLUB_ID);
        if(TextUtils.isEmpty(clubId)) {
            return;
        }

        if(!NetworkUtils.isOn(getBaseContext())) {
            showToast(R.string.no_connection);
            return;
        }

        ClubbookPreferences preferences = ClubbookPreferences.getInstance(getBaseContext());

        final View progressBar = findViewById(R.id.progressBar);
        final View txtYouHaventAccess = findViewById(R.id.txtYouHaventAccess);
        final View txtNoUsers = findViewById(R.id.txtNoUsers);
        final GridView gridUsers = (GridView) findViewById(R.id.gridUsers);

        progressBar.setVisibility(View.VISIBLE);

        HttpClientManager.getInstance().retrieveClubYesterdayCheckedInUsers(clubId, preferences.getAccessToken(), new HttpClientManager.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (isFinishing()) {
                    return;
                }

                progressBar.setVisibility(View.GONE);

                if (failed) {
                    if (result != null && result instanceof String && !((String) result).isEmpty()) {
                        txtYouHaventAccess.setVisibility(View.VISIBLE);
                    } else {
                        showToast(R.string.something_went_wrong_please_try_again);
                        txtYouHaventAccess.setVisibility(View.GONE);
                    }

                    txtNoUsers.setVisibility(View.GONE);
                    gridUsers.setVisibility(View.GONE);

                    return;
                }

                gridUsers.setVisibility(View.VISIBLE);

                List<User> users = (List<User>) result;

                if (users != null && !users.isEmpty()) {
                    mProfileAdapter = new ProfileAdapter(getBaseContext(), users, ProfileAdapter.MODE_DEFAULT);
                    gridUsers.setAdapter(mProfileAdapter);
                    gridUsers.setOnItemClickListener(YesterdayUsersGridActivity.this);
                } else {
                    txtNoUsers.setVisibility(View.GONE);
                }
            }
        });
    }
}
