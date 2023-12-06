package com.akshaj.model;

import com.google.gson.annotations.SerializedName;

public class Book {
    private String id;
    private  String title;
    private String subtitle;
    private String authors;
    private String image;
    private String url;
    private String publisher;
    private String pages;
    private String year;

    @SerializedName("download")
    private String downloadUrl;

    public Book(String id, String title, String subtitle, String authors, String image, String url) {

        this.id = id;
        this.title = title.replaceAll("[^a-zA-z0-9 ]","\\\\$0");
        this.subtitle = subtitle.replaceAll("[^a-zA-z0-9 ]","\\\\$0");
        this.authors = authors.replaceAll("[^a-zA-z0-9 ]","\\\\$0");
        this.image = image;
        this.url = url;
    }
    public Book(String id, String title, String subtitle, String authors, String image, String url, String publisher, String pages, String year, String download) {
        this.id = id;
        this.title = title.replaceAll("[^a-zA-z0-9 ]","\\\\$0");
        this.subtitle = subtitle.replaceAll("[^a-zA-z0-9 ]","\\\\$0");
        this.authors = authors.replaceAll("[^a-zA-z0-9 ]","\\\\$0");
        this.image = image;
        this.url = url;
        this.publisher = publisher;
        this.pages = pages;
        this.year = year;
        this.downloadUrl = download;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", authors='" + authors + '\'' +
                ", image='" + image + '\'' +
                ", url='" + url + '\'' +
                ", publisher='" + publisher + '\'' +
                ", pages='" + pages + '\'' +
                ", year='" + year + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}
