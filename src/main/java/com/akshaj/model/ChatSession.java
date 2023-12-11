package com.akshaj.model;

import org.telegram.telegrambots.meta.api.objects.User;

import com.akshaj.BotEnums.*;

public class ChatSession {
    //unique id of user, member of User class of telegram
    private User user;
    private  Long chatId;
    private UserState userState;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public UserState getCurrentBotState() {
        return userState;
    }

    public void setCurrentBotState(UserState userState) {
        this.userState = userState;
    }
}
