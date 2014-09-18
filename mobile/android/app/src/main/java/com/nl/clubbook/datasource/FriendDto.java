package com.nl.clubbook.datasource;

import org.json.JSONObject;

/**
 * Created by Volodymyr on 13.08.2014.
 */
public class FriendDto extends UserDto {

    public static String STATUS_FRIEND = "friend";
    public static String STATUS_RECEIVE_REQUEST = "receive_request";
    public static String STATUS_SENT_REQUEST = "sent_request";

    private String friendStatus;
    private boolean isBlocked = false;

    FriendDto() {
    }

    FriendDto(JSONObject userJson) {
        super(userJson);

        friendStatus = userJson.optString("friend_status", "");
        isBlocked = userJson.optBoolean("is_blocked", false);
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
