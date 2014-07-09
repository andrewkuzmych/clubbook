package com.nl.clubbook.datasource;

/**
 * Created by Andrew on 6/6/2014.
 */
public class ConversationShort {
    private String id;
    private String user_name;
    private String user_photo;
    private String user_id;
    private int unread_messages;
    private String last_message;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getUnread_messages() {
        return unread_messages;
    }

    public void setUnread_messages(int unread_messages) {
        this.unread_messages = unread_messages;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
