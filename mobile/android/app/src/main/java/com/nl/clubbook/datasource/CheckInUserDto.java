package com.nl.clubbook.datasource;

/**
 * Created by Volodymyr on 23.08.2014.
 */
public class CheckInUserDto {

    public static String DEFAULT_ID = "-100";

    private String id = DEFAULT_ID;
    private String name;
    private String gender;
    private String avatarUrl;
    private boolean isFriend;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }
}
