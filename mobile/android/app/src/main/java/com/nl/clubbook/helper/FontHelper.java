package com.nl.clubbook.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;

/**
 * Created by Volodymyr on 11.08.2014.
 */
public class FontHelper {
    private static FontHelper instance = null;

    private Typeface titilliumWebBold;
    private Typeface titilliumWebRegular;

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

    public Typeface getTitilliumWebBold() {
        return titilliumWebBold;
    }

    public void setTitilliumWebBold(Typeface titilliumWebBold) {
        this.titilliumWebBold = titilliumWebBold;
    }

    public Typeface getTitilliumWebRegular() {
        return titilliumWebRegular;
    }

    public void setTitilliumWebRegular(Typeface titilliumWebRegular) {
        this.titilliumWebRegular = titilliumWebRegular;
    }

    public void loadFont(final Context context) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                instance.setTitilliumWebBold(Typeface.createFromAsset(context.getAssets(), "fonts/TITILLIUMWEB-BOLD.TTF"));
                instance.setTitilliumWebRegular(Typeface.createFromAsset(context.getAssets(), "fonts/TITILLIUMWEB-REGULAR.TTF"));

                return null;
            }

        }.execute();
    }
}
