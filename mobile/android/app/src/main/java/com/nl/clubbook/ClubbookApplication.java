package com.nl.clubbook;

import android.app.Application;
import com.facebook.SessionLoginBehavior;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nl.clubbook.helper.FontHelper;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.helper.SessionManager;
import com.parse.Parse;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 5/17/14
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClubbookApplication extends Application {

    private Tracker mTracker;

    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "71OeWikSy4nxlGuefO2O6AFhuENP2Nqz1fjB88x3", "J5RrDFEhrHH7ns75OOWmNM4Wg52yEjkfxYAxvvDj");
        //PushService.setDefaultPushCallback(this, MainActivity.class);

        Permission[] permissions = new Permission[]{
                Permission.PUBLIC_PROFILE,
                Permission.EMAIL,
                Permission.USER_BIRTHDAY,
                Permission.USER_HOMETOWN
        };

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId("1504805883074351")
                .setPermissions(permissions)
                .setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK)
                .build();

        SimpleFacebook.setConfiguration(configuration);

        FontHelper.init(getApplicationContext());

        SessionManager.init(getApplicationContext());
        LocationCheckinHelper.init();
    }

    public synchronized Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.google_analitycs);
        }

        return mTracker;
    }
}
