package com.akshaj;

import com.akshaj.config.BotConfig;
import com.akshaj.config.ConfigLoader;
import com.akshaj.updatehandlers.BookHandler;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try{
            System.out.println("<< DBooksTelegramBot >>\nTo Debug or show Info on console \nUncomment print methods in BotLogger.java ");
            BotConfig botConfig = ConfigLoader.load("config.properties");
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            telegramBotsApi.registerBot(new BookHandler(botConfig.getBotName(),botConfig.getBotToken()));
        } catch (IOException | TelegramApiException e) {
            System.out.println(e.getMessage());
            BotLogger.logError("Exception caught in Main class",e);
            throw new RuntimeException(e);
        }
    }
}
