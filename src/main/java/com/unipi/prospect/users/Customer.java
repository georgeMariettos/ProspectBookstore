package com.unipi.prospect.users;

public class Customer extends User{

    private String address;
    public Customer(String username, String password, String name, String surname, String address) {
        super(username, password, name, surname);
        this.address = address;
    }

    public Customer(String username, String password, String name, String surname, boolean active, String address) {
        super(username, password, name, surname, active);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void AddBookToCart(){

    }
    public void RemoveBookFromCart(){

    }
    public void Checkout(){

    }
    public void ViewOrder(){
        // This method is now implemented in the CustomerController
        // to display orders in the customer.html template
    }
}
