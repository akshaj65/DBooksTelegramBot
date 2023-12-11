package com.akshaj.operations;

import com.akshaj.repository.BookRepository;
import com.akshaj.BotLogger;
import com.akshaj.model.Book;
import com.akshaj.model.ChatSession;
import com.akshaj.service.DBooksAPIClient;
import com.akshaj.utils.TelegramInterfaceHandler;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

import static com.akshaj.utils.Utils.getChatId;

public class BookInfoOperation implements Operation{

    private final DBooksAPIClient dBooksAPIClient;
    private final TelegramInterfaceHandler telegramInterfaceHandler;

    public BookInfoOperation(AbsSender absSender){
        dBooksAPIClient=new DBooksAPIClient();
        telegramInterfaceHandler=new TelegramInterfaceHandler(absSender);
    }
    @Override
    public void execute( Update update, Map<String, ChatSession> session)  {
        BotLogger.logInfo("BookInfo operation");

        CallbackQuery callbackQuery= update.getCallbackQuery();
        String booksId = update.getCallbackQuery().getData();
        Book book = dBooksAPIClient.getBookInfo(booksId);
        try {
            if (book != null) {
                BotLogger.logDebug("Book Not Null");
                telegramInterfaceHandler.sendBookInfoWithDownloadButton(getChatId(update), book);
                //set this so cache that we can cache  potential books
                BookRepository.getInstance().addBook(book);
                telegramInterfaceHandler.sendAnswerCallbackQuery("Book Found", false, callbackQuery);
            } else {
                telegramInterfaceHandler.sendAnswerCallbackQuery("Book Not Found", true, callbackQuery);
            }
        } catch (TelegramApiException e) {
            BotLogger.logError("BookInfo: "+e.getMessage(),e);
        }
    }

}
