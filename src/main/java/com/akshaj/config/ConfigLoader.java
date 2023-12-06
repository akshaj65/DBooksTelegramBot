package com.akshaj.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    public static BotConfig load(String filename) throws IOException {

        Properties prop = new Properties();
        InputStream inputStream= ConfigLoader.class.getClassLoader().getResourceAsStream(filename);
        if(inputStream==null){
            throw new IllegalArgumentException("File Not found"+filename);
        }
        prop.load(inputStream);

        BotConfig config = new BotConfig();
        config.setBotName(prop.getProperty("bot.name"));
        config.setBotToken(prop.getProperty("bot.token"));
        return config;

    }
}
