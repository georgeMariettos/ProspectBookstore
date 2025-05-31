package com.unipi.prospect.commerce;

import com.unipi.prospect.product.Item;

import java.sql.Date;
import java.util.ArrayList;

public class Order {
    private int id;
    private Date created;
    private String status;// Values: preparing - sent - delivered - cancelled
    private float total;
    private String address;
    private String username;
    private ArrayList<Item> items;

    // Use when getting already existing Orders
    public Order(int id, Date created, String status, String address, String username, ArrayList<Item> items) {
        this.id = id;
        this.created = created;
        this.status = status;
        this.address = address;
        this.username = username;
        this.items = items;

        if(items != null){
            calculateTotal();
        }else{
            this.total = 0;
        }

    }

    //Use for new orders
    public Order(Date created, String address, String username, ArrayList<Item> items){
        this(-1, created, "preparing", address, username, items);
    }

    public int getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public String getStatus() {
        return status;
    }

    public float getTotal() {
        return total;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
        calculateTotal();
    }

    private void calculateTotal(){
        float sum = 0;
        for(Item item : items){
            sum += item.getTotalPrice();
        }
        this.total = sum;
    }
}
