package com.nl.clubbook.datasource;

import org.json.JSONArray;
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
    protected String accessToken;
    protected boolean isNotificationEnabled;
    protected CheckInDto lastCheckIn;
    protected List<UserPhotoDto> photos;

    protected UserDto() {

    }

    protected UserDto(JSONObject userJson) {
        id = userJson.optString("_id");
        fb_id = userJson.optString("fb_id");
        name = userJson.optString("name");
        email = userJson.optString("email");
        gender = userJson.optString("gender");
        dob = userJson.optString("dob_format");
        age = userJson.optString("age");
        country = userJson.optString("country");
        bio = userJson.optString("bio");
        accessToken = userJson.optString("access_token");
        isNotificationEnabled = userJson.optBoolean("push", true);

        JSONArray photosJson = userJson.optJSONArray("photos");
        photos = new ArrayList<UserPhotoDto>();
        for (int i = 0; i < photosJson.length(); i++) {
            if (photosJson.optJSONObject(i).optBoolean("profile")) {
                avatar = photosJson.optJSONObject(i).optString("url");
            }

            photos.add(new UserPhotoDto(photosJson.optJSONObject(i)));
        }
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
        if("null".equalsIgnoreCase(age)) {
            age = "";
        }

        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCountry() {
        if(country != null && country.length() > 0) {
            char firstSymbol = country.charAt(0);
            country = country.replaceFirst(String.valueOf(firstSymbol), String.valueOf(firstSymbol).toUpperCase());

            return country;
        } else {
            return country;
        }
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

    public CheckInDto getLastCheckIn() {
        return lastCheckIn;
    }

    public void setLastCheckIn(CheckInDto lastCheckIn) {
        this.lastCheckIn = lastCheckIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isNotificationEnabled() {
        return isNotificationEnabled;
    }

    public void setNotificationEnabled(boolean isNotificationEnabled) {
        this.isNotificationEnabled = isNotificationEnabled;
    }
}
