package com.akshaj.operations;

import com.akshaj.exception.GeneralException;
import com.akshaj.model.ChatSession;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;

public interface Operation  {
    void execute( Update update);
}
