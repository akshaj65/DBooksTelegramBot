package com.akshaj.repository;

import com.akshaj.model.Book;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookRepository {

    private  static BookRepository instance=null;
    private final ConcurrentHashMap<String, Book> books;

    public static BookRepository getInstance(){
        if(instance==null){
            instance= new BookRepository(); //this calls the constructor once
        }
        return instance;
    }
    private BookRepository(){
        books=new ConcurrentHashMap<>();
    }


    public Map<String,Book> getBooks(){
        return books;
    }
    public void addBook(Book book){
        books.putIfAbsent(book.getId(),book);
    }

    public void removeBook(Book book){
          books.remove(book.getId());
    }

    public Book getBook(String bookId){
        return books.getOrDefault(bookId,null);
    }




}
