package com.akshaj.service;

import com.akshaj.BotLogger;
import com.akshaj.exception.GeneralException;
import com.akshaj.model.Book;
import com.akshaj.model.Books;

import java.util.ArrayList;
import java.util.List;

import static com.akshaj.Constants.*;

public class DBooksAPIClient {
    private static final String MY_BASE_URL="https://www.dbooks.org/api/";
    private final HttpClientWrapper httpClientWrapper;
    public DBooksAPIClient(){
        httpClientWrapper =new HttpClientWrapper();

    }

    public  List<Book> getRecentBooks(){
        String url=MY_BASE_URL+ RECENT;
        Books books= null;
        try {
            books = httpClientWrapper
                    .getFromUrl(url,
                     Books.class,
                    "Couldn't fetch recent books"
            );
        } catch (GeneralException e) {
            BotLogger.logError(e.getMessage(),e);
            return new ArrayList<>();
        }
        if(books==null){
            return new ArrayList<>();
        }
        return books.getBooksList();
    }

    public List<Book> searchBooks(String keyword){
        String url=MY_BASE_URL
                .concat(SEARCH)
                .concat(FORWARD_SLASH)
                .concat(keyword);
        Books books = null;
        try {
            books = httpClientWrapper
                    .getFromUrl(url,
                     Books.class,
                    "Search failed"
            );
        } catch (GeneralException e) {
            BotLogger.logError(e.getMessage(),e);
            return new ArrayList<>();
        }
        if(books==null){
            return new ArrayList<>();
        }
        return books.getBooksList();

    }

    public  Book getBookInfo(String bookId){
        String url=MY_BASE_URL
                .concat(BOOK_INFO)
                .concat(FORWARD_SLASH)
                .concat(bookId);
        try {
            return httpClientWrapper
                    .getFromUrl(url,
                            Book.class,
                            "Couldn't get book details"
                    );
        } catch (GeneralException e) {
            BotLogger.logError(e.getMessage(),e);
            return null;
        }
    }


}
