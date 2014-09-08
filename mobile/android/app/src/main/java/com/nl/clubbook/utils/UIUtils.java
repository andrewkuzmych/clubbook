package com.nl.clubbook.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.nl.clubbook.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * Created by Volodymyr on 21.08.2014.
 */
public class UIUtils {

    private UIUtils() {
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static void displayEmptyIconInActionBar(ActionBarActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_transparent);
    }

    public static void loadPhotoToActionBar(final ActionBarActivity activity, String url) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_transparent)
                .showImageForEmptyUri(R.drawable.ic_transparent)
                .showImageOnFail(R.drawable.ic_transparent)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        imageLoader.loadImage(url, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setIcon(new BitmapDrawable(activity.getResources(), bitmap));
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }
}
