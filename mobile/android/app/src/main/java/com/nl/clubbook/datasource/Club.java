package com.nl.clubbook.datasource;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Andrew on 5/27/2014.
 */
public class Club implements Parcelable {

    private String id;
    private String title;
    private String phone;
    private String address;
    private double lat;
    private double lon;
    private String avatar;
    private String info;
    private String ageRestriction;
    private String dressCode;
    private String capacity;
    private String website;
    private String email;
    private float distance;
    private int activeCheckIns;
    private int activeFriendsCheckIns;
    private ClubWorkingHours todayWorkingHours;
    private List<ClubWorkingHours> workingHours;
    private List<String> photos = new ArrayList<String>();
    private List<CheckInUser> users; //TODO

    public Club() {
    }

    public Club(Parcel in) {
        id = in.readString();
        title = in.readString();
        phone = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        avatar = in.readString();
        info = in.readString();
        ageRestriction = in.readString();
        dressCode = in.readString();
        capacity = in.readString();
        website = in.readString();
        email = in.readString();
        distance = in.readFloat();
        activeCheckIns = in.readInt();
        activeFriendsCheckIns = in.readInt();
        todayWorkingHours = in.readParcelable(ClubWorkingHours.class.getClassLoader());

        Parcelable[] hoursParc = in.readParcelableArray(ClubWorkingHours.class.getClassLoader());
        ClubWorkingHours[] hoursArr = null;
        if (hoursParc != null) {
            hoursArr = Arrays.copyOf(hoursParc, hoursParc.length, ClubWorkingHours[].class);
        }
        if(hoursArr != null) {
            workingHours = Arrays.asList(hoursArr);
        }

        in.readStringList(photos);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CheckInUser> getUsers() {
        return users;
    }

    public void setUsers(List<CheckInUser> users) {
        this.users = users;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Integer getActiveCheckIns() {
        return activeCheckIns;
    }

    public void setActiveCheckIns(Integer activeCheckIns) {
        this.activeCheckIns = activeCheckIns;
    }

    public int getActiveFriendsCheckIns() {
        return activeFriendsCheckIns;
    }

    public void setActiveFriendsCheckIns(int activeFriendsCheckIns) {
        this.activeFriendsCheckIns = activeFriendsCheckIns;
    }

    public ClubWorkingHours getTodayWorkingHours() {
        return todayWorkingHours;
    }

    public void setTodayWorkingHours(ClubWorkingHours todayWorkingHours) {
        this.todayWorkingHours = todayWorkingHours;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(String ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public String getDressCode() {
        return dressCode;
    }

    public void setDressCode(String dressCode) {
        this.dressCode = dressCode;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ClubWorkingHours> getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(List<ClubWorkingHours> workingHours) {
        this.workingHours = workingHours;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(avatar);
        dest.writeString(info);
        dest.writeString(ageRestriction);
        dest.writeString(dressCode);
        dest.writeString(capacity);
        dest.writeString(website);
        dest.writeString(email);
        dest.writeFloat(distance);
        dest.writeInt(activeCheckIns);
        dest.writeInt(activeFriendsCheckIns);
        dest.writeParcelable(todayWorkingHours, flags);

        ClubWorkingHours[] hours = new ClubWorkingHours[workingHours.size()];
        workingHours.toArray(hours);
        dest.writeParcelableArray(hours, flags);

        dest.writeStringList(photos);
    }

    public static final Creator<Club> CREATOR = new Creator<Club>() {
        @Override
        public Club createFromParcel(Parcel source) {
            return new Club(source);
        }

        @Override
        public Club[] newArray(int size) {
            return new Club[size];
        }
    };
}
