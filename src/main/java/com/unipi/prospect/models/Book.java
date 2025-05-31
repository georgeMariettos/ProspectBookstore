package com.unipi.prospect.models;

import java.util.List;

public class Book {
    private String isbn;
    private String title;
    private List<String> authorUsername;
    private String imageUrl;
    private String description;
    private String publisher;
    private String publishedDate;
    private int pageCount;
    private List<String> genre;
    private String previewLink;

    public Book() {
    }

    public Book(String isbn, String title, List<String> authorUsername, String imageUrl) {
        this.isbn = isbn;
        this.title = title;
        this.authorUsername = authorUsername;
        this.imageUrl = imageUrl;
    }

    public Book(String isbn, String title, List<String> authorUsername, String imageUrl,
                String description, String publisher, String publishedDate,
                int pageCount, List<String> genre, String previewLink) {
        this.isbn = isbn;
        this.title = title;
        this.authorUsername = authorUsername;
        this.imageUrl = imageUrl;
        this.description = description;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.pageCount = pageCount;
        this.genre = genre;
        this.previewLink = previewLink;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(List<String> authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }
}
