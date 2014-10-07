package com.nl.clubbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.nl.clubbook.R;

/**
 * Created by Volodymyr on 07.10.2014.
 */
public class FindFriendsActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_find_friends);

        initActionBar(R.string.find_friends);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtInviteFriends:
                onInviteFriendsClicked();
                break;
        }
    }

    private void initView() {
        findViewById(R.id.txtInviteFriends).setOnClickListener(this);

    }

    private void onInviteFriendsClicked() {
        Intent intent = new Intent(getBaseContext(), InviteFriends.class);
        startActivity(intent);
    }
}
