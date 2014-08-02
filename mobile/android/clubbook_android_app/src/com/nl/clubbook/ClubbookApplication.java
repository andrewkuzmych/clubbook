package com.nl.clubbook;

import android.app.Application;
import com.facebook.SessionLoginBehavior;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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

    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "71OeWikSy4nxlGuefO2O6AFhuENP2Nqz1fjB88x3", "J5RrDFEhrHH7ns75OOWmNM4Wg52yEjkfxYAxvvDj");
        //PushService.setDefaultPushCallback(this, MainActivity.class);


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);

        Permission[] permissions = new Permission[]{
                //Permission.USER_ABOUT_ME,
                // Permission.USER_BIRTHDAY,
                Permission.BASIC_INFO,
                Permission.EMAIL,
                // Permission.BASIC_INFO,
                Permission.USER_FRIENDS
                //Permission.PUBLISH_ACTION
        };

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId("1504805883074351")         //    369491643181697
                        //.setNamespace("sromkuapp")
                .setPermissions(permissions)
                .setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK)
                .build();

        SimpleFacebook.setConfiguration(configuration);
    }
}
