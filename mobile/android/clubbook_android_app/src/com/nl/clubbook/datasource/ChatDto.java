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
    private Integer unreadMessages = 0;
    private List<ChatMessageDto> conversation;
    private UserDto currentUser;
    private UserDto receiver;

    public ChatDto() {
    }

    public ChatDto(JSONObject chatJson) throws JSONException {
        JSONArray conversation_dto = chatJson.getJSONArray("conversation");

        List<ChatMessageDto> conversation = new ArrayList<ChatMessageDto>();
        for (int i = 0; i < conversation_dto.length(); i++) {
            conversation.add(new ChatMessageDto(conversation_dto.getJSONObject(i)));
        }
        setConversation(conversation);

        setCurrentUser(new UserDto(chatJson.getJSONObject("current_user")));
        setReceiver(new UserDto(chatJson.getJSONObject("receiver")));
        if(chatJson.has("chat_id")) {
            // new chat between 2 people
            setChatId(chatJson.getString("chat_id"));
        }

        if(chatJson.has("unread_messages")){
            setUnreadMessages(chatJson.getInt("unread_messages"));
        }
    }


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

    public Integer getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(Integer unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

}
