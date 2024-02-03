package com.akshaj.operations;

import com.akshaj.BotLogger;
import com.akshaj.exception.GeneralException;
import com.akshaj.model.Book;
import com.akshaj.model.ChatSession;
import com.akshaj.repository.ChatSessionRepository;
import com.akshaj.service.DBooksAPIClient;
import com.akshaj.utils.TelegramInterfaceHandler;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.Map;

import static com.akshaj.BotEnums.KeyboardType.CANCEL_KEYBOARD;
import static com.akshaj.BotEnums.KeyboardType.MAIN_KEYBOARD;
import static com.akshaj.BotEnums.UserState.RECENT_STATE;
import static com.akshaj.utils.TelegramInterfaceHandler.*;
import static com.akshaj.utils.Utils.getChatId;
import static com.akshaj.utils.Utils.getUserId;

public class RecentOperation implements Operation{
    private final DBooksAPIClient dBooksAPIClient;
    private final TelegramInterfaceHandler telegramInterfaceHandler;

    public RecentOperation(AbsSender absSender){
        dBooksAPIClient=new DBooksAPIClient();
        telegramInterfaceHandler=new TelegramInterfaceHandler(absSender);

    }
    @Override
    public void execute( Update update)  {
        BotLogger.logDebug("Recent Operation");
        String userId=getUserId(update);
        ChatSession session= ChatSessionRepository.getInstance().getSession(userId);
        session.setCurrentBotState(RECENT_STATE);
        Long chatId= session.getChatId();
        List<Book> bookList= dBooksAPIClient.getRecentBooks();
        if(!bookList.isEmpty()) {
            telegramInterfaceHandler.sendMsgWithKeyboard(chatId,"Recent Books",CANCEL_KEYBOARD);
            session.setCurrentBookList(bookList);
            telegramInterfaceHandler.showBooksInInlineKeyboard(chatId,bookList);

        }else {
            telegramInterfaceHandler.sendMsgWithKeyboard( chatId,"No Recent Books found",MAIN_KEYBOARD); //  no books then add main keyboard
        }
    }
}
