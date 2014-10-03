package com.nl.clubbook.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.nl.clubbook.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    public static void loadPhotoToActionBar(final ActionBarActivity activity, String url, Target target) {
        Picasso.with(activity).load(url).into(target);
    }
}
