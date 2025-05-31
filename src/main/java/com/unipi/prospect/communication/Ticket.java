package com.unipi.prospect.communication;

import java.sql.Date;

public class Ticket {
    private int id;
    private String comment;
    private String username;
    private Date creationDate;
    private String status;  //Values: opened - resolved

    // Use when getting already existing tickets
    public Ticket(int id, String comment, String username, Date creationDate, String status) {
        this.id = id;
        this.comment = comment;
        this.username = username;
        this.creationDate = creationDate;
        this.status = status;
    }

    //Use for new Tickets
    public Ticket(String comment, String username, Date creationDate) {
        this(-1, comment, username, creationDate, "opened");
    }

    public int getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public String getUsername() {
        return username;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getStatus() {
        return status;
    }
}
