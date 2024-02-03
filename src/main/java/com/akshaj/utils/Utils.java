package com.akshaj.utils;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class Utils {
    public static String getUserId(Update update){
        if(update.hasMessage()){
            return update.getMessage().getFrom().getId().toString();
        }else if(update.hasCallbackQuery()){
            return update.getCallbackQuery().getFrom().getId().toString();
        }
        return null;
    }

    public static Long getChatId(Update update){
        if(update.hasMessage()){
            return update.getMessage().getChatId();
        }else if(update.hasCallbackQuery()){
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return null;
    }
    public static Integer getMessageId(Update update){
        if(update.hasMessage()){
            return update.getMessage().getMessageId();
        }else if(update.hasCallbackQuery()){
            return update.getCallbackQuery().getMessage().getMessageId();
        }
        return null;
    }

    public static User getUser(Update update){
        return update.hasMessage()
                ? update.getMessage().getFrom()
                : update.getCallbackQuery().getFrom();

    }
}
