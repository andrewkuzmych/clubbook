package com.nl.clubbook.datasource;

import java.util.List;

/**
 * Created by Andrew on 6/12/2014.
 */
public class Chat {
    private String chatId;
    private List<Conversation> conversation;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<Conversation> getConversation() {
        return conversation;
    }

    public void setConversation(List<Conversation> conversation) {
        this.conversation = conversation;
    }
}
