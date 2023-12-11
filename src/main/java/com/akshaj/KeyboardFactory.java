package com.akshaj;

import com.akshaj.model.Book;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.akshaj.Constants.*;

public class KeyboardFactory {

    public static class ReplyKeyboardMarkupFactory{
        public static  ReplyKeyboardMarkup createSingleButtonKeyboardMarkup(String name) {

            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(END_SERVICE);

            // Create a keyboard
            return createCustomReplyKeyboardMarkup(List.of(keyboardRow));

        }


        public static  ReplyKeyboardMarkup createMainMenuKeyboardMarkup() {

            // First keyboard row
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            keyboardFirstRow.add(RECENT_COMMAND);

            // Second keyboard row
            KeyboardRow keyboardSecondRow =new KeyboardRow();
            keyboardSecondRow.add(SEARCH_COMMAND);

            List<KeyboardRow> keyboardRows = List.of(keyboardFirstRow,keyboardSecondRow);

            // Create a keyboard
            return createCustomReplyKeyboardMarkup(keyboardRows);
        }

        private static ReplyKeyboardMarkup createCustomReplyKeyboardMarkup(List<KeyboardRow> keyboardRows) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(false);
            return replyKeyboardMarkup;
        }
    }

    public static class InlineKeyboardMarkupFactory{
        public static InlineKeyboardMarkup createBooksKeyboardMarkup(List<Book> bookList) {
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            int count=1;
            for( Book book : bookList){
                if(count==20){
                    break;
                }
                count++;
                InlineKeyboardButton button= new InlineKeyboardButton(book.getTitle());
                button.setCallbackData(book.getId());
                keyboard.add(List.of(button));  //single button each row

            }
            return new InlineKeyboardMarkup(keyboard);
        }
        public static InlineKeyboardMarkup createSingleButtonKeyboardMarkup(String name,String callbackData) {

            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton keyboardButton = new InlineKeyboardButton(name);
            keyboardButton.setCallbackData(callbackData);
            row.add(keyboardButton);

            return  new InlineKeyboardMarkup(List.of(row));
        }
    }



}
