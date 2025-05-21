package com.unipi.prospect.models;

import java.util.List;

public class Book {
    private String isbn;
    private String title;
    private List<String> authors;
    private String imageUrl;
    private String description;
    private String publisher;
    private String publishedDate;
    private int pageCount;
    private List<String> categories;
    private String previewLink;

    public Book() {
    }

    public Book(String isbn, String title, List<String> authors, String imageUrl) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.imageUrl = imageUrl;
    }

    public Book(String isbn, String title, List<String> authors, String imageUrl,
                String description, String publisher, String publishedDate,
                int pageCount, List<String> categories, String previewLink) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.imageUrl = imageUrl;
        this.description = description;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.pageCount = pageCount;
        this.categories = categories;
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

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
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

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }
}
