package com.unipi.prospect.communication;

import java.sql.Date;

public class Comment {
    private int id;
    private String username;
    private Date dateCreated;
    private String content;
    private String bookIsbn;
    private int replyTo;
    private int rating;

    //Use for already existing Comments.
    public Comment(int id, String username, Date dateCreated, String content, String bookIsbn, int replyTo, int rating) {
        this.id = id;
        this.username = username;
        this.dateCreated = dateCreated;
        this.content = content;
        this.bookIsbn = bookIsbn;
        this.replyTo = replyTo;
        this.rating = rating;
    }

    //Use for new comments
    public Comment(String username, Date dateCreated, String content, String bookIsbn, int replyTo, int rating) {
        this(-1, username, dateCreated, content, bookIsbn, replyTo, rating);
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getContent() {
        return content;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public int getReplyTo() {
        return replyTo;
    }

    public int getRating() {
        return rating;
    }
}
