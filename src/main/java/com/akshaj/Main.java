package com.akshaj;

import com.akshaj.config.BotConfig;
import com.akshaj.config.ConfigLoader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try{
            BotConfig botConfig = ConfigLoader.load("config.properties");

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
