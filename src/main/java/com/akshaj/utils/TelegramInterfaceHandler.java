package com.akshaj.utils;

import com.akshaj.BotEnums;
import com.akshaj.BotLogger;
import com.akshaj.KeyboardFactory;
import com.akshaj.repository.TFilesRepository;
import com.akshaj.model.Book;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.akshaj.BotEnums.KeyboardType.MAIN_KEYBOARD;
import static com.akshaj.Constants.DOWNLOAD;
import static com.akshaj.Constants.END_SERVICE;

public class TelegramInterfaceHandler {

    private final AbsSender absSender;

    public TelegramInterfaceHandler(AbsSender absSender){
        this.absSender=absSender;

    }

    public  void sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackQuery) throws TelegramApiException {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setText(text);
        answerCallbackQuery.setShowAlert(alert);
        absSender.execute(answerCallbackQuery);
    }

    public  synchronized void sendMsgWithKeyboard(Long chatId,String msg, BotEnums.KeyboardType keyboardType)  {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true); // can make text bold
        message.setChatId(chatId);
        message.setText(msg);
        if ( MAIN_KEYBOARD.equals(keyboardType)){
            message.setReplyMarkup(
                    KeyboardFactory.ReplyKeyboardMarkupFactory
                            .createMainMenuKeyboardMarkup());
        }else{
            message.setReplyMarkup(
                    KeyboardFactory.ReplyKeyboardMarkupFactory
                            .createSingleButtonKeyboardMarkup(END_SERVICE));
        }

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            BotLogger.logError("Can't Send Message Error: "+e.getMessage()   ,e);
        }
    }


    public void showBooksInInlineKeyboard(Long chatId, List<Book> bookList) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose a Book to get Info:");
        message.setReplyMarkup(
                KeyboardFactory.InlineKeyboardMarkupFactory.createBooksKeyboardMarkup(bookList)
        );

        try {
              absSender.execute(message);

//            Message sentMessage=absSender.execute(message);
//            deletableMsgChatIdMsgId[0]=chatId.toString();
//            deletableMsgChatIdMsgId[1]=sentMessage.getMessageId().toString();
        } catch (TelegramApiException e) {
            BotLogger.logError("Could Not Send Message"+e.getMessage(),e);
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
                BotLogger.logDebug("Setting sendDocument");
                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(chatId);
                sendDocument.setDocument(inputFile);
                sendDocument.setCaption(fileName.concat(fileType));
                BotLogger.logDebug("Finished Setting ");

                Message message=absSender.execute(sendDocument);
                String fileId = message.getDocument().getFileId();
//                sendMsg(userId,chatId,"Dive into your book now");
                BotLogger.logDebug("File Sent");
                BotLogger.logDebug("bookId "+book.getId());
                TFilesRepository.getInstance().addFile(book.getId(),fileId);
            } catch (IOException | TelegramApiException e) {
                BotLogger.logError("Error while downloading",e);
                sendMsgWithKeyboard(chatId,"Error while downloading "+book.getTitle() +e.getMessage(),
                            MAIN_KEYBOARD);
            }
        });

    }
    public void sendFile(Long chatId, String  fileId) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(new InputFile(fileId));

        try {
            absSender.execute(sendDocument);
        } catch (TelegramApiException e) {
          BotLogger.logError("Failed to sendFile",e);
        }
    }

    public void sendBookInfoWithDownloadButton(Long chatId,Book book) {
        try {

            InputFile inputFile = fetchPhotoAsInputFile(book);
            String content = appendBookContent(book);

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setParseMode(ParseMode.MARKDOWN);
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(inputFile);
            sendPhoto.setCaption(content);


            // create keyboard
            String buttonCallbackData = DOWNLOAD.concat(book.getId());


            //set replyMarkup
            sendPhoto.setReplyMarkup(
                    KeyboardFactory.InlineKeyboardMarkupFactory
                            .createSingleButtonKeyboardMarkup("Download", buttonCallbackData)
            );


            absSender.execute(sendPhoto);

        } catch (IOException | TelegramApiException e) {
            BotLogger.logError(e.getMessage(),e);
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






}
