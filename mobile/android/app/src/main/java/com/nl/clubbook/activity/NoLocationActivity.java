package com.nl.clubbook.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.nl.clubbook.R;

/**
 * Created by odats on 18/06/2014.
 */
public class NoLocationActivity extends BaseActivity {

    public static final String ACTION_CLOSE = "NoLocationActivity.ACTION_CLOSE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_location);

        registerReceiver(mCloseActivityReceiver, new IntentFilter(ACTION_CLOSE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mCloseActivityReceiver != null) {
            unregisterReceiver(mCloseActivityReceiver);
        }
    }

    private BroadcastReceiver mCloseActivityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            NoLocationActivity.this.finish();
        }
    };
}