package com.nl.clubbook.datasource;

import org.json.JSONObject;

/**
 * Created by Volodymyr on 13.08.2014.
 */
public class FriendDto extends UserDto {

    public static String STATUS_FRIEND = "friend";

    private String friendStatus;

    FriendDto() {
    }

    FriendDto(JSONObject userJson) {
        super(userJson);

        friendStatus = userJson.optString("friend_status", "");
    }

    public String getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }
}
