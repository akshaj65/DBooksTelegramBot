package com.akshaj.operations;

import com.akshaj.BotLogger;
import com.akshaj.exception.GeneralException;
import com.akshaj.model.ChatSession;
import com.akshaj.service.DBooksAPIClient;
import com.akshaj.utils.TelegramInterfaceHandler;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;

import static com.akshaj.BotEnums.KeyboardType.MAIN_KEYBOARD;
import static com.akshaj.BotEnums.UserState.START_STATE;
import static com.akshaj.utils.Utils.*;

public class StartOperation implements Operation{
    private final TelegramInterfaceHandler telegramInterfaceHandler;

    public StartOperation(AbsSender absSender){
        telegramInterfaceHandler=new TelegramInterfaceHandler(absSender);
    }

    @Override
    public void execute(Update update, Map<String, ChatSession> session) {
        BotLogger.logInfo("Start Operation");
            String userId= getUserId(update);
            session.get(userId).setCurrentBotState(START_STATE);
            Long chatId=session.get(userId).getChatId();
            String firstName=session.get(userId)
                    .getUser()
                    .getFirstName()
                    .replaceAll("[!#$_`*|=+\\-{\\[.'\"<)}]","\\\\$0");

            String message="""
                Hi  %s
                Welcome to MyBooks Bot
                
                Explore the services in the menu
                """.formatted(
                    firstName);

            telegramInterfaceHandler.sendMsgWithKeyboard(chatId,message, MAIN_KEYBOARD);


    }
}
