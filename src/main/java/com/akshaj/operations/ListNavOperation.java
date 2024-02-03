package com.akshaj.operations;

import com.akshaj.BotLogger;
import com.akshaj.KeyboardFactory;
import com.akshaj.model.Book;
import com.akshaj.model.ChatSession;
import com.akshaj.repository.ChatSessionRepository;
import com.akshaj.utils.TelegramInterfaceHandler;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.akshaj.Constants.*;
import static com.akshaj.KeyboardFactory.InlineKeyboardMarkupFactory.createKeyboardRows;
import static com.akshaj.utils.Utils.getUserId;

public class ListNavOperation implements Operation {

    private final TelegramInterfaceHandler telegramInterfaceHandler;
    public ListNavOperation(AbsSender absSender){
        telegramInterfaceHandler = new TelegramInterfaceHandler(absSender);
    }
    @Override
    public void execute(Update update) {

        BotLogger.logInfo("ListNav operation");

        CallbackQuery callbackQuery= update.getCallbackQuery();

        String callbackData= callbackQuery.getData();
        ChatSession session= ChatSessionRepository.getInstance().getSession(getUserId(update));

        BotLogger.logDebug( "Callback Data "+callbackQuery.getData());
        int currentPageNum=session.getPageNum();
        List<Book> currentBookList=session.getCurrentBookList();
        if(currentBookList==null){
            return;
        }
        BotLogger.logDebug("PageNum -- "+currentPageNum);

        if(callbackData.equals(PREV)){
            if(currentPageNum>0){
                session.setPageNum(--currentPageNum);
            }
        }else if(callbackData.equals(NEXT)){
            if(currentPageNum * PAGE_OFFSET < currentBookList.size()){
                session.setPageNum(++currentPageNum);
            }
        }
        InlineKeyboardMarkup keyboardMarkup =new InlineKeyboardMarkup(
                                                         createKeyboardRows(session.getCurrentBookList(),currentPageNum));

        telegramInterfaceHandler.editInlineKeyboard(callbackQuery,keyboardMarkup);
        try {
            int totalPages = currentBookList.size() / PAGE_OFFSET;
                    telegramInterfaceHandler.sendAnswerCallbackQuery(currentPageNum+" / "+(totalPages-1),false,callbackQuery);
        } catch (TelegramApiException e) {
            BotLogger.logError("Can't Send Message Error: "+e.getMessage()   ,e);
        }


    }
}
