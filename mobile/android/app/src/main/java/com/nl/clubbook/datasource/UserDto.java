package com.nl.clubbook.datasource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 5/19/2014.
 */
public class UserDto {

    protected String id;
    protected String fb_id;
    protected String name;
    protected String email;
    protected String password;
    protected String gender;
    protected String dob;
    protected String age;
    protected String avatar;
    protected String country;
    protected String bio;
    protected List<UserPhotoDto> photos;

    protected UserDto() {

    }

    protected UserDto(JSONObject userJson) {
        this.setId(userJson.optString("_id"));
        if (userJson.has("fb_id"))
            this.setFb_id(userJson.optString("fb_id"));
        this.setName(userJson.optString("name"));
        if (userJson.has("email"))
            this.setEmail(userJson.optString("email"));
        this.setGender(userJson.optString("gender"));
        if (userJson.has("dob_format"))
            this.setDob(userJson.optString("dob_format"));
        if (userJson.has("age") && userJson.optString("age") != "null")
            this.setAge(userJson.optString("age"));
        if (userJson.has("country"))
            this.setCountry(userJson.optString("country"));
        if (userJson.has("bio"))
            this.setBio(userJson.optString("bio"));

        JSONArray photos_json = userJson.optJSONArray("photos");
        List<UserPhotoDto> photos = new ArrayList<UserPhotoDto>();
        for (int i = 0; i < photos_json.length(); i++) {
            if (photos_json.optJSONObject(i).optBoolean("profile"))
                this.setAvatar(photos_json.optJSONObject(i).optString("url"));

            photos.add(new UserPhotoDto(photos_json.optJSONObject(i)));
        }
        this.setPhotos(photos);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<UserPhotoDto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<UserPhotoDto> photos) {
        this.photos = photos;
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

}
