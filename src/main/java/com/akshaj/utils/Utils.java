package com.akshaj.utils;

import org.telegram.telegrambots.meta.api.objects.Update;

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
}
