package com.nl.clubbook.datasource;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andrew on 6/6/2014.
 */
public class ChatMessageDto {
    private String user_from;
    private String msg;
    private String type;

    public ChatMessageDto() {
    }

    public ChatMessageDto(JSONObject jsonObject) throws JSONException {
        setUser_from(jsonObject.getString("from_who"));
        setMsg(jsonObject.getString("msg"));
        setType(jsonObject.getString("type"));
    }

    public String getUser_from() {
        return user_from;
    }

    public void setUser_from(String user_from) {
        this.user_from = user_from;
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
}
