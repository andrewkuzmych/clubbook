package com.nl.clubbook.datasource;

import org.json.JSONObject;

/**
 * Created by odats on 05/08/2014.
 */
public class UserPhotoDto {
    private String id;
    private String url;
    private Boolean isAvatar;

    public UserPhotoDto() {
    }

    UserPhotoDto(JSONObject rawData) {
        this.setId(rawData.optString("_id"));
        this.setUrl(rawData.optString("url"));
        this.setIsAvatar(rawData.optBoolean("profile"));
    }

    public Boolean getIsAvatar() {
        return isAvatar;
    }

    public void setIsAvatar(Boolean isAvatar) {
        this.isAvatar = isAvatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
