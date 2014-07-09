package com.nl.clubbook.datasource;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andrew on 6/6/2014.
 */
public class ChatMessageDto {
    private String userFrom;
    private String userFromName;
    private String userFromAvatar;
    private String msg;
    private String type;
    private Boolean isMyMessage;

    public ChatMessageDto() {
    }

    public ChatMessageDto(String msg) {
        setMsg(msg);
        setType("message");
        setIsMyMessage(true);
    }

    public ChatMessageDto(JSONObject jsonObject) throws JSONException {
        setType(jsonObject.getString("type"));
        if(getType().equalsIgnoreCase("message") && jsonObject.has("msg")) {
            setMsg(jsonObject.getString("msg"));
        }
        if(jsonObject.has("is_my_message"))
            setIsMyMessage(jsonObject.getBoolean("is_my_message"));
        else
            setIsMyMessage(false);

        setUserFrom(jsonObject.getString("from_who"));
        setUserFromName(jsonObject.getString("from_who_name"));
        setUserFromAvatar(jsonObject.getString("from_who_avatar"));
    }

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsMyMessage() {
        return isMyMessage;
    }

    public void setIsMyMessage(Boolean isMyMessage) {
        this.isMyMessage = isMyMessage;
    }

    public String getUserFromAvatar() {
        return userFromAvatar;
    }

    public void setUserFromAvatar(String userFromAvatar) {
        this.userFromAvatar = userFromAvatar;
    }

    public String getUserFromName() {
        return userFromName;
    }

    public void setUserFromName(String userFromName) {
        this.userFromName = userFromName;
    }
}
