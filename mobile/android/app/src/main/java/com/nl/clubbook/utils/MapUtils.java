package com.nl.clubbook.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.nl.clubbook.R;

import java.util.Locale;

/**
 * Created by Volodymyr on 21.01.2015.
 */
public class MapUtils {

    private MapUtils() {
    }

    public static void showLocationOnGoogleMapApp(Context context, double lat, double lon) {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lon);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.complete_action_using)));
    }
}
