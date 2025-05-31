package com.unipi.prospect.commerce;

public class Card {
    public float amount;
    private String type;
    private String expiryDate;
    private  String number;

    public Card(float amount, String type, String expiryDate, String number) {
        this.amount = amount;
        this.type = type;
        this.expiryDate = expiryDate;
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getNumber() {
        return number;
    }

    public void Authorize(){
        //Bank Call
    }
}
