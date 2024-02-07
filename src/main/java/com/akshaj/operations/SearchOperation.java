package com.akshaj.operations;

import com.akshaj.BotLogger;
import com.akshaj.Constants;
import com.akshaj.model.Book;
import com.akshaj.model.ChatSession;
import com.akshaj.repository.ChatSessionRepository;
import com.akshaj.service.DBooksAPIClient;
import com.akshaj.utils.TelegramInterfaceHandler;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

import static com.akshaj.BotEnums.KeyboardType.CANCEL_KEYBOARD;
import static com.akshaj.BotEnums.UserState.SEARCH_STATE;
import static com.akshaj.utils.Utils.getUserId;

public class SearchOperation implements Operation{
    private final DBooksAPIClient dBooksAPIClient;
    private final TelegramInterfaceHandler telegramInterfaceHandler;
    public SearchOperation(AbsSender absSender){
        dBooksAPIClient=new DBooksAPIClient();
        telegramInterfaceHandler=new TelegramInterfaceHandler(absSender);
    }
    @Override
    public void execute(Update update) {
        BotLogger.logInfo("Search Operation");

        String message=update.getMessage().getText();
        String userId= getUserId(update);
        ChatSession session= ChatSessionRepository.getInstance().getSession(userId);
        session.setCurrentBotState(SEARCH_STATE);
        Long chatId= session.getChatId();

        if(message.equals(Constants.SEARCH_COMMAND)){
            BotLogger.logDebug("Search Phase 1");
            String txt = "Search your Books by a keyword\n" +
                    "Type a word from your book title";
            telegramInterfaceHandler.sendMsgWithKeyboard( chatId, txt, CANCEL_KEYBOARD);
        }else{
            BotLogger.logDebug("Search Phase 2");
            List<Book> bookList = dBooksAPIClient.searchBooks(message);
            BotLogger.logDebug("working");
            if (null!=bookList && !bookList.isEmpty()) {
                BotLogger.logDebug("bookList Not empty");
                session.setCurrentBookList(bookList);
                telegramInterfaceHandler.showBooksInInlineKeyboard(chatId, bookList);
            } else {
                BotLogger.logDebug("book Not found");
                telegramInterfaceHandler.sendMsgWithKeyboard( chatId,"Book Not found", CANCEL_KEYBOARD);

            }
        }
    }

}
