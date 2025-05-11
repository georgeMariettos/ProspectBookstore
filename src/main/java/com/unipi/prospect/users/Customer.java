package com.unipi.prospect.users;

public class Customer extends User{

    private String address;
    public Customer(String username, String password, String name, String address) {
        super(username, password, name);
        this.address = address;
    }

    public void AddBookToCart(){

    }
    public void RemoveBookFromCart(){

    }
    public void Checkout(){

    }
    public void ViewOrder(){

    }
}
