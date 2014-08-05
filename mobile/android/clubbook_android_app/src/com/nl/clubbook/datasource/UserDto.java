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

    private String id;
    private String fb_id;
    private String name;
    private String email;
    private String password;
    private String gender;
    private String dob;
    private String age;
    private String avatar;
    private String country;
    private String bio;
    private List<UserPhotoDto> photos;

    UserDto() {

    }

    UserDto(JSONObject userJson) throws JSONException {
        this.setId(userJson.getString("_id"));
        if (userJson.has("fb_id"))
            this.setFb_id(userJson.getString("fb_id"));
        this.setName(userJson.getString("name"));
        if (userJson.has("email"))
            this.setEmail(userJson.getString("email"));
        this.setGender(userJson.getString("gender"));
        if (userJson.has("dob_format"))
            this.setDob(userJson.getString("dob_format"));
        if (userJson.has("age") && userJson.getString("age") != "null")
            this.setAge(userJson.getString("age"));
        if (userJson.has("country"))
            this.setCountry(userJson.getString("country"));
        if (userJson.has("bio"))
            this.setBio(userJson.getString("bio"));

        JSONArray photos_json = userJson.getJSONArray("photos");
        List<UserPhotoDto> photos = new ArrayList<UserPhotoDto>();
        for (int i = 0; i < photos_json.length(); i++) {
            if (photos_json.getJSONObject(i).getBoolean("profile"))
                this.setAvatar(photos_json.getJSONObject(i).getString("url"));

            photos.add(new UserPhotoDto(photos_json.getJSONObject(i)));
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
