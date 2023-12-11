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
            BotConfig botConfig = ConfigLoader.load("config.properties");
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            telegramBotsApi.registerBot(new BookHandler(botConfig.getBotName(),botConfig.getBotToken()));
        } catch (IOException | TelegramApiException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
