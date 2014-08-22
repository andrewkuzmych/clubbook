package com.nl.clubbook.datasource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 6/12/2014.
 */
public class ChatDto {
    private String chatId;
    private int unreadMessages;
    private List<ChatMessageDto> conversation;
    private UserDto currentUser;
    private UserDto receiver;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<ChatMessageDto> getConversation() {
        return conversation;
    }

    public void setConversation(List<ChatMessageDto> conversation) {
        this.conversation = conversation;
    }

    public UserDto getReceiver() {
        return receiver;
    }

    public void setReceiver(UserDto receiver) {
        this.receiver = receiver;
    }

    public UserDto getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserDto currentUser) {
        this.currentUser = currentUser;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

}
