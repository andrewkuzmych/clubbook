package com.nl.clubbook.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;

/**
 * Created by Volodymyr on 11.08.2014.
 */
public class FontHelper {
    private static FontHelper instance = null;

    private Typeface helveticaBold;
    private Typeface helveticaRegular;

    private FontHelper() {
    }

    public static FontHelper getInstance(Context context) {
        if(instance == null) {
            init(context);
        }

        return instance;
    }

    /*
     * init FontHelper in Application in onCreate() method
     */
    public static void init(Context context) {
        if(instance == null) {
            instance = new FontHelper();
            instance.loadFont(context);
        }
    }

    public Typeface getHelveticaBold() {
        return helveticaBold;
    }

    public void setHelveticaBold(Typeface helveticaBold) {
        this.helveticaBold = helveticaBold;
    }

    public Typeface getHelveticaRegular() {
        return helveticaRegular;
    }

    public void setHelveticaRegular(Typeface helveticaRegular) {
        this.helveticaRegular = helveticaRegular;
    }

    public void loadFont(final Context context) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                instance.setHelveticaBold(Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica_Bold.ttf"));
                instance.setHelveticaRegular(Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica_Normal.ttf"));

                return null;
            }

        }.execute();
    }
}
