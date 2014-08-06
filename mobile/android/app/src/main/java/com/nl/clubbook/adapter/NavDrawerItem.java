package com.nl.clubbook.adapter;

public class NavDrawerItem {

    private String title;
    private String gender;
    private String age;
    private int icon;
    private Integer count = 0;
    private String profileAvatar;
    private boolean isProfile = false;
    private String background = "#240E25";

    public NavDrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public NavDrawerItem(String title, int icon, Integer count) {
        this.title = title;
        this.icon = icon;
        this.count = count;
    }

    public NavDrawerItem(String profile_name, String profile_avatar, String profile_gender, String profile_age) {
        this.title = profile_name;
        this.profileAvatar = profile_avatar;
        this.gender = profile_gender;
        this.age = profile_age;
        this.isProfile = true;
    }

    public String getProfileAvatar() {
        return profileAvatar;
    }

    public void setProfileAvatar(String profileAvatar) {
        this.profileAvatar = profileAvatar;
    }

    public String getTitle() {
        return this.title;
    }

    public int getIcon() {
        return this.icon;
    }

    public Integer getCount() {
        return this.count;
    }

    public boolean getProfileVisibility() {
        return this.isProfile;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
