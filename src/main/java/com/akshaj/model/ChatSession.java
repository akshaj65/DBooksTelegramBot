package com.akshaj.model;

import org.telegram.telegrambots.meta.api.objects.User;

import com.akshaj.BotEnums.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

public class ChatSession {
    //unique id of user, member of User class of telegram
    private User user;
    private  Long chatId;
    private UserState userState;

    private  int pageNum;
    private List<Book> currentBookList;

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
    public List<Book> getCurrentBookList() {
        return currentBookList;
    }

    public void setCurrentBookList(List<Book> currentBookList) {
        this.currentBookList = currentBookList;
    }
    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }


}
