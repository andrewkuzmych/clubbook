package com.nl.clubbook.datasource;

import android.graphics.Bitmap;

/**
 * Created by Volodymyr on 03.10.2014.
 */
public class Contact {

    private long id;
    private String name;
    private String email;
    private Bitmap photo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
