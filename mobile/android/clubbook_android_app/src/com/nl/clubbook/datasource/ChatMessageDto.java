package com.nl.clubbook.datasource;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andrew on 6/6/2014.
 */
public class ChatMessageDto {
    private String userFrom;
    private String msg;
    private String type;
    private Boolean isMyMessage;

    public ChatMessageDto() {
    }

    public ChatMessageDto(JSONObject jsonObject) throws JSONException {
        setUserFrom(jsonObject.getString("from_who"));
        setMsg(jsonObject.getString("msg"));
        setType(jsonObject.getString("type"));
        setIsMyMessage(jsonObject.getBoolean("is_my_message"));
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
}
