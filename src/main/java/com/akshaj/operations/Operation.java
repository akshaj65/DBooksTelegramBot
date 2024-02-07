package com.akshaj.operations;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public interface Operation  {
    void execute( Update update);
}
