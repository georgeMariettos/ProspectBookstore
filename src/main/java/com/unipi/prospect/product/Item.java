package com.unipi.prospect.product;

public class Item {
    private String isbn;
    private int quantity;
    private float price;
    private float totalPrice;

    public Item(String isbn, int quantity, float price){
        this.isbn = isbn;
        this.quantity = quantity;
        this.totalPrice = quantity * price;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
        this.totalPrice = quantity * price;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public float getTotalPrice() {
        return totalPrice;
    }
}
