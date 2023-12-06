package com.akshaj.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Books {
    private String status;
    private String total;
    @SerializedName("books")
    private List<Book> booksList;

    public Books(String status, String total, List<Book> booksList) {
        this.status = status;
        this.total = total;
        this.booksList = booksList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Book> getBooksList() {
        return booksList;
    }

    public void setBooksList(List<Book> booksList) {
        this.booksList = booksList;
    }
}
