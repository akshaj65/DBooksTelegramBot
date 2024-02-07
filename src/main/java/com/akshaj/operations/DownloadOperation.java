package com.akshaj.operations;

import com.akshaj.repository.BookRepository;
import com.akshaj.BotLogger;
import com.akshaj.repository.TFilesRepository;
import com.akshaj.model.Book;
import com.akshaj.utils.TelegramInterfaceHandler;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.akshaj.BotEnums.KeyboardType.MAIN_KEYBOARD;
import static com.akshaj.Constants.DOWNLOAD;

public class DownloadOperation implements Operation{
    private final TelegramInterfaceHandler telegramInterfaceHandler;

    public DownloadOperation(AbsSender absSender){
        telegramInterfaceHandler=new TelegramInterfaceHandler(absSender);
    }
    @Override
    public void execute(Update update){
        try {
            BotLogger.logInfo("Download operation");

            CallbackQuery callbackQuery= update.getCallbackQuery();

            String downloadCallbackData= callbackQuery.getData();

            BotLogger.logDebug( "Callback Data "+callbackQuery.getData());
            String bookId = downloadCallbackData.replaceFirst(DOWNLOAD,"");
            BotLogger.logDebug("BookId -- "+bookId);

            Long chatId=callbackQuery.getMessage().getChatId();

            String fileId = TFilesRepository.getInstance().getFileId(bookId);
            if(null!=fileId){
                BotLogger.logDebug("Download: FileId found");
                telegramInterfaceHandler.sendFile(chatId,fileId);
                telegramInterfaceHandler.sendAnswerCallbackQuery("Found File",true,callbackQuery);
            }else{
                Book book = BookRepository.getInstance().getBook(bookId);
                if(null!=book && null!=book.getDownloadUrl()){
                    BotLogger.logDebug("Download: Book found");
                    telegramInterfaceHandler.sendAnswerCallbackQuery("Download initiated",true,callbackQuery);

                    telegramInterfaceHandler.sendMsgWithKeyboard(chatId," Enjoy your time while we fetch your book",MAIN_KEYBOARD);

                    telegramInterfaceHandler.sendPdfFileAsync(chatId,book);
                }else{
                    BotLogger.logDebug("Download: Book Not found");
                    telegramInterfaceHandler.sendAnswerCallbackQuery("Sorry Not Found!",false,callbackQuery);
                }

            }
        }catch (TelegramApiException e) {
            BotLogger.logError("Error while download operation"+ e.getMessage(),e);
        }
    }
}
