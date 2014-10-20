package com.nl.clubbook.datasource;

import java.util.List;

/**
 * Created by Andrew on 6/12/2014.
 */
public class Chat {
    private String chatId;
    private int unreadMessages;
    private List<ChatMessage> conversation;
    private User currentUser;
    private User receiver;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<ChatMessage> getConversation() {
        return conversation;
    }

    public void setConversation(List<ChatMessage> conversation) {
        this.conversation = conversation;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

}
