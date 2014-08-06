package com.nl.clubbook.adapter;

import android.graphics.Bitmap;

/**
 * Created by Andrew on 6/2/2014.
 */
public class ProfileItem {
    private Bitmap image;
    private String title;

    public ProfileItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
