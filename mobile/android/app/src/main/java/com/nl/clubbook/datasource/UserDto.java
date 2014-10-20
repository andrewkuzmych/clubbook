package com.nl.clubbook.datasource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 5/19/2014.
 */
public class UserDto {

    public static String STATUS_FRIEND = "friend";
    public static String STATUS_RECEIVE_REQUEST = "receive_request";
    public static String STATUS_SENT_REQUEST = "sent_request";

    protected String id;
    protected String accessToken;
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
    private String friendStatus;
    protected boolean isNotificationEnabled;
    private boolean isBlocked = false;
    protected CheckIn lastCheckIn;
    protected List<UserPhoto> photos;

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
        photos = new ArrayList<UserPhoto>();
        for (int i = 0; i < photosJson.length(); i++) {
            if (photosJson.optJSONObject(i).optBoolean("profile")) {
                avatar = photosJson.optJSONObject(i).optString("url");
            }

            photos.add(new UserPhoto(photosJson.optJSONObject(i)));
        }

        friendStatus = userJson.optString("friend_status", "");
        isBlocked = userJson.optBoolean("is_blocked", false);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<UserPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<UserPhoto> photos) {
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

    public CheckIn getLastCheckIn() {
        return lastCheckIn;
    }

    public void setLastCheckIn(CheckIn lastCheckIn) {
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

    public String getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }
}
