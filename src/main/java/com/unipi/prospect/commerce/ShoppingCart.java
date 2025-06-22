package com.unipi.prospect.commerce;

import com.unipi.prospect.product.Item;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Locale;

public class ShoppingCart {
    private Date created;
    private float total;
    private ArrayList<Item> items;

    public ShoppingCart(Date created, ArrayList<Item> items) {
        this.created = created;
        this.items = items;
        calculateTotal();
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
        calculateTotal();
    }

    public Date getCreated() {
        return created;
    }

    public float getTotal() {
        return total;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void calculateTotal(){
        float sum = 0;
        for(Item item : items){
            sum += item.getTotalPrice();
        }

        sum = Float.parseFloat(String.format(Locale.US, "%.2f", sum));
        this.total = sum;
    }
}
