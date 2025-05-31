package com.unipi.prospect.product;

public class Book {
    private final String isbn;
    private String title;
    private String authorUsername;
    private String imageUrl;
    private String description;
    private String publisher;
    private String publishedDate;
    private int pageCount;
    private String genre;
    private String previewLink;
    private float price;
    private int stock;

    public Book(String isbn, String title, String authorUsername, String imageUrl, String description, String publisher, String publishedDate, int pageCount, String genre, String previewLink, float price, int stock) {
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
        this.price = price;
        this.stock = stock;
    }

    public Book(String isbn, String title, String authorUsername, String imageUrl, String description, String publisher, String publishedDate, int pageCount, String genre, String previewLink) {
        this(isbn, title, authorUsername, imageUrl, description, publisher, publishedDate, pageCount, genre, previewLink, 0, 0);
    }

    public Book(String isbn, String title, String authorUsername, String imageUrl) {
        this.isbn = isbn;
        this.title = title;
        this.authorUsername = authorUsername;
        this.imageUrl = imageUrl;
    }

    public void setStock(int stock) {
        this.stock = Math.max(stock, 0);
    }

    public void setPrice(float price) {
        this.price = Math.max(price, 0);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getPreviewLink() {
        return previewLink;
    }
}
