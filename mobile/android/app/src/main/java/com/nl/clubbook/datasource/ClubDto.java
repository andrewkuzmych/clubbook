package com.nl.clubbook.datasource;

import java.util.List;

/**
 * Created by Andrew on 5/27/2014.
 */
public class ClubDto {
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
    private ClubWorkingHoursDto todayWorkingHours;
    private List<ClubWorkingHoursDto> workingHours;
    private List<String> photos;
    private List<UserDto> users;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<UserDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserDto> users) {
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

    public ClubWorkingHoursDto getTodayWorkingHours() {
        return todayWorkingHours;
    }

    public void setTodayWorkingHours(ClubWorkingHoursDto todayWorkingHours) {
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

    public List<ClubWorkingHoursDto> getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(List<ClubWorkingHoursDto> workingHours) {
        this.workingHours = workingHours;
    }
}
