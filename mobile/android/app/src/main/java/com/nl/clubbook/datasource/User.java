package com.nl.clubbook.datasource;

import android.os.Parcel;
import android.os.Parcelable;

import com.nl.clubbook.utils.ConvertUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Andrew on 5/19/2014.
 */
public class User implements Parcelable {

    public static String STATUS_FRIEND = "friend";
    public static String STATUS_RECEIVE_REQUEST = "receive_request";
    public static String STATUS_SENT_REQUEST = "sent_request";

    protected String id;
    protected String accessToken;
    protected String fbId;
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
    protected List<UserPhoto> photos = new ArrayList<UserPhoto>();

    public User() {
    }

    public User(Parcel in) {
        id = in.readString();
        accessToken = in.readString();
        fbId = in.readString();
        name = in.readString();
        email = in.readString();
        password = in.readString();
        gender = in.readString();
        dob = in.readString();
        age = in.readString();
        avatar = in.readString();
        country = in.readString();
        bio = in.readString();
        friendStatus = in.readString();
        isNotificationEnabled = ConvertUtils.intToBoolean(in.readInt());
        isBlocked = ConvertUtils.intToBoolean(in.readInt());
        lastCheckIn = in.readParcelable(CheckIn.class.getClassLoader());

        Parcelable[] userPhotosParcelable = in.readParcelableArray(UserPhoto.class.getClassLoader());
        UserPhoto[] userPhotos;
        if (userPhotosParcelable != null) {
            userPhotos = Arrays.copyOf(userPhotosParcelable, userPhotosParcelable.length, UserPhoto[].class);
            photos = Arrays.asList(userPhotos);
        }
    }

    public User(JSONObject userJson) {
        id = userJson.optString("_id");
        fbId = userJson.optString("fb_id");
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

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(accessToken);
        dest.writeString(fbId);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(gender);
        dest.writeString(dob);
        dest.writeString(age);
        dest.writeString(avatar);
        dest.writeString(country);
        dest.writeString(bio);
        dest.writeString(friendStatus);
        dest.writeInt(ConvertUtils.booleanToInt(isNotificationEnabled));
        dest.writeInt(ConvertUtils.booleanToInt(isBlocked));
        dest.writeParcelable(lastCheckIn, flags);

        UserPhoto[] userPhotos = new UserPhoto[photos.size()];
        photos.toArray(userPhotos);
        dest.writeParcelableArray(userPhotos, flags);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
