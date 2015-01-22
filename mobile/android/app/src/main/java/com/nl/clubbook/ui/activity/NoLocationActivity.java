package com.nl.clubbook.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.nl.clubbook.R;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.utils.L;

/**
 * Created by odats on 18/06/2014.
 */
public class NoLocationActivity extends BaseActivity implements View.OnClickListener {

    public static final String ACTION_CLOSE = "NoLocationActivity.ACTION_CLOSE";
    public static final String ACTION_LOCATION_PROVIDER_ENABLED = "NoLocationActivity.ACTION_LOCATION_PROVIDER_ENABLED";

    public static final String EXTRA_IS_PROGRESS_ENABLED = "EXTRA_IS_PROGRESS_ENABLED";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_location);

        registerReceiver(mCloseActivityReceiver, new IntentFilter(ACTION_CLOSE));
        registerReceiver(mLocationProviderEnabledReceiver, new IntentFilter(ACTION_LOCATION_PROVIDER_ENABLED));

        if(!LocationCheckinHelper.getInstance().isLocationTrackerStarted()) {
            Intent intent = new Intent(NoLocationActivity.this, MainLoginActivity.class);
            startActivity(intent);
            return;
        }

        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleExtras();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mCloseActivityReceiver != null) {
            unregisterReceiver(mCloseActivityReceiver);
        }

        if(mLocationProviderEnabledReceiver != null) {
            unregisterReceiver(mLocationProviderEnabledReceiver);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.txtOpenSettings) {
            onOpenSettingsClicked();
        }
    }

    private void initView() {
        findViewById(R.id.txtOpenSettings).setOnClickListener(this);

        handleExtras();
    }

    private void handleExtras() {
        boolean showProgress = getIntent().getBooleanExtra(EXTRA_IS_PROGRESS_ENABLED, false);
        if(!showProgress) {
            setProgressViewVisibility(LocationCheckinHelper.getInstance().isLocationProvidersEnabled());
        }
    }

    private void onOpenSettingsClicked() {
        try {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        } catch (ActivityNotFoundException e) {
            L.i("" + e);
        }
    }

    private void setProgressViewVisibility(boolean isVisible) {
        if(isVisible) {
            findViewById(R.id.holderNoLocation).setVisibility(View.GONE);
            findViewById(R.id.holderProgressView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.holderNoLocation).setVisibility(View.VISIBLE);
            findViewById(R.id.holderProgressView).setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver mCloseActivityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            NoLocationActivity.this.finish();
        }
    };

    private BroadcastReceiver mLocationProviderEnabledReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setProgressViewVisibility(true);
        }
    };
}