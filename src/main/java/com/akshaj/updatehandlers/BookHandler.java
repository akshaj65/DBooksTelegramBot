package com.akshaj.updatehandlers;


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

public class BookHandler extends TelegramLongPollingBot {

    private final String botName;
    private String userId;
    private  Long chatId;
    private Update currentUpdate;
    private  CallbackQuery callbackQuery;
    private UserState currentUserState;
    private final  Map<String,String> files=new HashMap<>();
    private final Map<String, ChatSession> sessionMap=new HashMap<>();


    private boolean isMessage;
    private  DBooksAPIClient dbooksAPiClient;

    private Operation startOperation,recentOperation,downloadOperation,searchOperation,bookInfoOperation;


    private  String[] deletableMsgChatIdMsgId=new String[2];

    private final Logger logger= LoggerFactory.getLogger(BookHandler.class);


    public BookHandler(String botName,String botToken){
        super(botToken);
        this.botName=botName;
        initialize();

    }

    private void initialize() {
        dbooksAPiClient = new DBooksAPIClient();
        startOperation= new StartOperation(this);
        recentOperation= new RecentOperation(this);
        searchOperation= new SearchOperation(this);
        bookInfoOperation= new BookInfoOperation(this);
        downloadOperation=new DownloadOperation(this);
    }


    @Override
    public void onUpdateReceived(Update update) {
       BotLogger.logDebug("Current Files: "+files);
       BotLogger.logDebug("Current Session: "+sessionMap);
       initOnUpdate(update);

        if ( update.hasCallbackQuery() && !update.getCallbackQuery().getData().isEmpty()){
            callbackQuery = update.getCallbackQuery();
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
        currentUpdate=update;
        isMessage=update.hasMessage();
        initUserIdChatId();
        addUserDataIfNotPresent();
        currentUserState= sessionMap.get(userId).getCurrentBotState();
    }

    private void initUserIdChatId() {
        if(isMessage){
            userId=currentUpdate.getMessage().getFrom().getId().toString();
            chatId=currentUpdate.getMessage().getChatId();
        }else {
            userId=currentUpdate.getCallbackQuery().getFrom().getId().toString();
            chatId=currentUpdate.getCallbackQuery().getMessage().getChatId();

        }
    }


    private void handleIncomingCallback(Update update) {
        String callbackData=callbackQuery.getData();
        if (callbackData.contains(DOWNLOAD)){

            downloadOperation.execute(update,sessionMap);
        }else if (RECENT_STATE.equals(currentUserState)  || SEARCH_STATE.equals(currentUserState) ){
            BotLogger.logDebug(currentUserState.toString());

            bookInfoOperation.execute(update,sessionMap);

        }
    }

    private void handleIncomingMessage(Update update) {
        String message = update.getMessage().getText();
        switch (message) {
            case START_COMMAND -> {
                startOperation.execute(update,sessionMap);
            }
            case RECENT_COMMAND -> {
                recentOperation.execute(update,sessionMap);
            }
            case SEARCH_COMMAND-> {

                searchOperation.execute(update,sessionMap);
            }
            case END_SERVICE -> {
//                deleteMessage(deletableMsgChatIdMsgId[0], deletableMsgChatIdMsgId[1]);
                sessionMap.get(userId).setCurrentBotState(NONE_STATE);
                sendMsgWithKeyboard("Main Menu",MAIN_KEYBOARD);
            }
            default -> {
                if(SEARCH_STATE.equals(currentUserState)){
                    searchOperation.execute(update,sessionMap);
                    return;
                }
                sessionMap.get(userId).setCurrentBotState(NONE_STATE);
            }
        }
    }

    private void searchOperation() {
        sessionMap.get(userId).setCurrentBotState(SEARCH_STATE);
        StringBuffer sb = new StringBuffer();
        sb.append("Search your Books by a keyword\n");
        sb.append("Type a word from your book title");
        sendMsgWithKeyboard( sb.toString(), CANCEL_KEYBOARD);
    }

    private void recentOperation() {
        sessionMap.get(userId).setCurrentBotState(RECENT_STATE);

        List<Book> bookList= dbooksAPiClient.getRecentBooks();
        if(!bookList.isEmpty()) {
            sendMsgWithKeyboard("Recent Books",CANCEL_KEYBOARD);
            showBooksInInlineKeyboard(chatId,bookList);
        }else {
            sendMsgWithKeyboard( "No Recent Books found",MAIN_KEYBOARD); //  no books then add main keyboard
        }
    }

    private void bookInfoOperation()  {
        Long chatId = sessionMap.get(userId).getChatId();
        String booksId = callbackQuery.getData();
        Book book = dbooksAPiClient.getBookInfo(booksId);
        System.out.println(101);
        try {
            if (book != null) {
                System.out.println(book);
                System.out.println(82 + " " + chatId);
                sendBookInfoWithDownloadButton(chatId, book);
                //set this so cache that we can cache  potential books
                BookRepository.getInstance().addBook(book);
                sendAnswerCallbackQuery("Book Found", false, callbackQuery);
            } else {
                sendAnswerCallbackQuery("Book Not Found", true, callbackQuery);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadOperation() {
        try {
            String callbackQueryData= callbackQuery.getData();
            System.out.println( callbackQuery.getData());
            String bookId = callbackQueryData.replaceFirst(DOWNLOAD,"");
            System.out.println("67 k -- "+bookId);
            System.out.println();
            String fileId =TFilesRepository.getInstance().getFileId(bookId);
            if(null!=fileId){
                System.out.println("70");
                sendFile(chatId,fileId);
                sendAnswerCallbackQuery("Found File",true,callbackQuery);
            }else{
                Book book = BookRepository.getInstance().getBook(bookId);
                if(null!=book && null!=book.getDownloadUrl()){
                    logger.info("71");
                    sendAnswerCallbackQuery("Download initiated",true,callbackQuery);

                    sendMsgWithKeyboard(" Enjoy your time while we fetch your book",MAIN_KEYBOARD);

                    sendPdfFileAsync(chatId,book);
                }else{
                    System.out.println("73");
                    sendAnswerCallbackQuery("Sorry Not Found!",false,callbackQuery);
                }

            }
        }catch (TelegramApiException e) {
            BotLogger.logError("Error while download operation  Error:"+ e.getMessage(),e);
        }
    }

    private void deleteMessage(String chatId, String messageId) {
        if( null == chatId || null == messageId){
            return;
        }
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(Integer.valueOf(messageId));
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            BotLogger.logError("Error deleting message: "+e.getMessage(),e);
        }

        //reset it
        deletableMsgChatIdMsgId[0]=null;
        deletableMsgChatIdMsgId[1]=null;
    }

    private void startOperation() {
        sessionMap.get(userId).setCurrentBotState(START_STATE);

        String firstName=sessionMap.get(userId).getUser().getFirstName().replaceAll("[!#$_`*|=+\\-{\\[.'\"<)}]","\\\\$0");
        String message="""
                Hi  %s
                Welcome to MyBooks Bot
                
                Explore the services in the menu
                """.formatted(
                firstName);

        sendMsgWithKeyboard(message, MAIN_KEYBOARD);

    }

    /**
     *
     * @param msg message to deliver
     * @param keyboardType  which keyboard we need to set
     */
    public synchronized void sendMsgWithKeyboard(String msg, KeyboardType keyboardType){
        SendMessage message = new SendMessage();
        message.enableMarkdown(true); // can make text bold
        message.setChatId(chatId);
        message.setText(msg);
        if ( MAIN_KEYBOARD.equals(keyboardType)){
            message.setReplyMarkup(
                    ReplyKeyboardMarkupFactory
                            .createMainMenuKeyboardMarkup());
        }else{
            message.setReplyMarkup(
                    ReplyKeyboardMarkupFactory
                            .createSingleButtonKeyboardMarkup(END_SERVICE));
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            BotLogger.logError("Can't Send Message",e);
        }
    }


    private void showBooksInInlineKeyboard(Long chatId,List<Book> bookList){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose a Book to get Info:");
        message.setReplyMarkup(
                InlineKeyboardMarkupFactory.createBooksKeyboardMarkup(bookList)
        );

        try {
            Message sentMessage=execute(message);
            deletableMsgChatIdMsgId[0]=chatId.toString();
            deletableMsgChatIdMsgId[1]=sentMessage.getMessageId().toString();
        } catch (TelegramApiException e) {
            BotLogger.logError("Can't Send Books Keyboard",e);
        }

    }




    private void sendBookInfoWithDownloadButton(Long chatId,Book book)  {
        try {

            InputFile inputFile=fetchPhotoAsInputFile(book);
            String content=appendBookContent(book);

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setParseMode(ParseMode.MARKDOWN);
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(inputFile);
            sendPhoto.setCaption(content);


            // create keyboard
            String buttonCallbackData=DOWNLOAD.concat(book.getId());


            //set replyMarkup
            sendPhoto.setReplyMarkup(
                    InlineKeyboardMarkupFactory
                            .createSingleButtonKeyboardMarkup("Download",buttonCallbackData)
            );


            execute(sendPhoto);

        } catch (IOException | TelegramApiException e) {
            System.out.println("craftBookMessage " + e.getMessage());

        }

    }


    private InputFile fetchPhotoAsInputFile(Book book) throws IOException {
        URL url =new URL(book.getImage());

        InputFile inputFile= new InputFile();
        InputStream inputStream = url.openStream();
        inputFile.setMedia(inputStream,book.getTitle());
        return inputFile;
    }

    private String appendBookContent(Book book) {
        return """
                *Id:* `%s`
                *Title:* `%s`
                *SubTitle:* `%s`
                *Authors:* `%s`
                *Publisher:* `%s`
                *Year:* `%s`
                *Pages:* `%s`
                """
                .formatted(
                        book.getId(),book.getTitle(),book.getSubtitle(),
                        book.getAuthors(),book.getPublisher(),book.getYear(),book.getPages()
                );
    }


    /**
     *
     * @param text   the text that should be shown
     * @param alert  If the text should be shown as alert or not
     * @param callbackQuery
     * @throws TelegramApiException
     */
    private void sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackQuery) throws TelegramApiException {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setText(text);
        answerCallbackQuery.setShowAlert(alert);
        execute(answerCallbackQuery);
    }

    private void addUserDataIfNotPresent() {
        if(!sessionMap.containsKey(userId)) {
            User user = isMessage
                    ? currentUpdate.getMessage().getFrom()
                    : currentUpdate.getCallbackQuery().getFrom();

            ChatSession userData = new ChatSession();
            userData.setChatId(chatId);
            if (user != null) {
                userData.setUser(user);
            } else {
                System.out.println("user is null");
            }
            sessionMap.put(userId, userData);
        }
    }

    public void sendPdfFileAsync(Long chatId,Book book) {
        CompletableFuture.runAsync(()->{
            try {
                String fileName = book.getTitle().replaceAll(" ","-");
                String fileType =".pdf";
                URL url = new URL(book.getDownloadUrl());

                InputStream inputStream;
                inputStream = url.openStream();

                InputFile inputFile = new InputFile();
                inputFile.setMedia(inputStream,fileName.concat(fileType));
                System.out.println(415);
                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(chatId);
                sendDocument.setDocument(inputFile);
                sendDocument.setCaption(fileName.concat(fileType));
                System.out.println(420);

                Message message=execute(sendDocument);
                String fileId = message.getDocument().getFileId();
//                sendMsg(userId,chatId,"Dive into your book now");
                System.out.println(425);
                System.out.println(book.getId());
                TFilesRepository.getInstance().addFile(book.getId(),fileId);
            } catch (IOException | TelegramApiException e) {
                System.out.println(e.getMessage());
                sendMsgWithKeyboard("Error while downloading "+book.getTitle() +e.getMessage(),
                        MAIN_KEYBOARD);
            }
        });

    }
    public void sendFile(Long chatId, String  fileId) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(new InputFile(fileId));

        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    @Override
    public String getBotUsername() {
        return botName;
    }
}
