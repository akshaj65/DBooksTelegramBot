package com.akshaj.updatehandlers;


import com.akshaj.repository.ChatSessionRepository;
import com.akshaj.utils.TelegramInterfaceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.akshaj.repository.BookRepository;
import com.akshaj.BotEnums.*;
import com.akshaj.BotLogger;
import com.akshaj.repository.TFilesRepository;
import com.akshaj.model.*;
import com.akshaj.operations.*;
import com.akshaj.service.DBooksAPIClient;

import static com.akshaj.BotEnums.KeyboardType.*;
import static com.akshaj.BotEnums.UserState.*;
import static com.akshaj.Constants.*;
import static com.akshaj.KeyboardFactory.*;
import static com.akshaj.utils.Utils.*;

public class BookHandler extends TelegramLongPollingBot {

    private final String botName;
    private String userId;
    private  Long chatId;
    private UserState currentUserState;
    private ChatSessionRepository chatSessionRepository= ChatSessionRepository.getInstance();

    private Operation startOperation,recentOperation,downloadOperation,searchOperation,bookInfoOperation,listNavOperation;

    private TelegramInterfaceHandler telegramInterfaceHandler;

//    private  String[] deletableMsgChatIdMsgId=new String[2];
    private ArrayList<Book> currentBookList;

    private final Logger logger= LoggerFactory.getLogger(BookHandler.class);


    public BookHandler(String botName,String botToken){
        super(botToken);
        this.botName=botName;
        initialize();

    }

    private void initialize() {
        telegramInterfaceHandler = new TelegramInterfaceHandler(this);
        startOperation= new StartOperation(this);
        recentOperation= new RecentOperation(this);
        searchOperation= new SearchOperation(this);
        bookInfoOperation= new BookInfoOperation(this);
        listNavOperation= new ListNavOperation(this);
        downloadOperation=new DownloadOperation(this);
    }


    @Override
    public void onUpdateReceived(Update update) {
       BotLogger.logDebug("No of Files: "+TFilesRepository.getInstance().getNoOfFileIds());
       BotLogger.logDebug("Current Session: "+chatSessionRepository.getNoOfSessions());
       initOnUpdate(update);

        if ( update.hasCallbackQuery() && !update.getCallbackQuery().getData().isEmpty()){
           BotLogger.logDebug("callBack");

           BotLogger.logDebug("current State "+currentUserState);

            handleIncomingCallback(update);

        }
        else if (update.hasMessage() && update.getMessage().hasText() ){

           BotLogger.logDebug("message");
           BotLogger.logDebug("prev State "+currentUserState);
            handleIncomingMessage(update);

        }
    }



    /**
     *
     *  This method initializes values
     * @param update passes the update
     */
    private void initOnUpdate(Update update) {
//        variables and methods  should follow the same sequence.
        initUserIdChatId(update);
        addUserDataIfNotPresent(update);

        currentUserState= chatSessionRepository.getSession(userId).getCurrentBotState();
    }

    private void initUserIdChatId(Update update) {
        userId=getUserId(update);
        chatId = getChatId(update);
    }


    private void handleIncomingCallback(Update update) {
        String callbackData=update.getCallbackQuery().getData();
        if (callbackData.contains(DOWNLOAD)){
            downloadOperation.execute(update);
        }else if(PREV.equals(callbackData) || NEXT.equals(callbackData)){
            listNavOperation.execute(update);
        }else if (RECENT_STATE.equals(currentUserState)  || SEARCH_STATE.equals(currentUserState) ){
            BotLogger.logDebug(currentUserState.toString());
            bookInfoOperation.execute(update);
        }
    }

    private void handleIncomingMessage(Update update) {
        String message = update.getMessage().getText();

        switch (message) {
            case START_COMMAND -> {
                startOperation.execute(update);
            }
            case RECENT_COMMAND -> {
                recentOperation.execute(update);
            }
            case SEARCH_COMMAND-> {
                searchOperation.execute(update);
            }
            case END_SERVICE -> {
//                deleteMessage(deletableMsgChatIdMsgId[0], deletableMsgChatIdMsgId[1]);
                chatSessionRepository.getSession(userId).setCurrentBotState(NONE_STATE);
                telegramInterfaceHandler.sendMsgWithKeyboard(chatId,"Main Menu",MAIN_KEYBOARD);
            }
            default -> {
                if(SEARCH_STATE.equals(currentUserState)){
                    searchOperation.execute(update);
                    return;
                }
                chatSessionRepository.getSession(userId).setCurrentBotState(NONE_STATE);
            }
        }
    }

    private void addUserDataIfNotPresent(Update update) {
        if(chatSessionRepository.getSession(userId)==null) {
            User user = getUser(update);

            ChatSession userData = new ChatSession();
            userData.setChatId(chatId);
            if (user != null) {
                userData.setUser(user);
            } else {
                BotLogger.logInfo("user is null");
            }
            chatSessionRepository.addSession(userId, userData);
        }
    }


    @Override
    public String getBotUsername() {
        return botName;
    }
}
