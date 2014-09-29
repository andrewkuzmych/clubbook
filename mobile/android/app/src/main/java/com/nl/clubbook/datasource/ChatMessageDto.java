package com.nl.clubbook.datasource;

import android.content.Context;

import com.nl.clubbook.R;

/**
 * Created by Andrew on 6/6/2014.
 */
public class ChatMessageDto extends BaseChatMessage {
    public static final String TYPE_MESSAGE = "message";
    public static final String TYPE_DRINK = "drink";
    public static final String TYPE_SMILE = "smile";

    private String userFrom;
    private String userFromName;
    private String userFromAvatar;
    private String msg;
    private String type;
    private long time;
    private boolean isMyMessage;
    private boolean isRead;

    public String getFormatMessage(Context context){
        String result;

        if(TYPE_MESSAGE.equalsIgnoreCase(type)){
            result = msg;

        } else if(TYPE_SMILE.equalsIgnoreCase(type)) {
            result = userFromName + " " + context.getString(R.string.likes_the_profile);

        } else if(TYPE_DRINK.equalsIgnoreCase(type)) {
            result = userFromName + " " + context.getString(R.string.invites_for_a_drink);

        } else {
            result = "";
        }

        return result;
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

    public boolean getIsMyMessage() {
        return isMyMessage;
    }

    public void setIsMyMessage(boolean isMyMessage) {
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
}
