package com.nl.clubbook.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.nl.clubbook.R;

/**
 * Created by Volodymyr on 07.10.2014.
 */
public class InviteFriends extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_invite_friends);

        initActionBar(R.string.invite_friends);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
