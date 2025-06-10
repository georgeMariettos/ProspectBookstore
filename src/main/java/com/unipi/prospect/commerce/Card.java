package com.unipi.prospect.commerce;

public class Card {
    public float amount;
    private String type;
    private String expiryDate;
    private String number;
    private String holderName;
    private String CVV;

    public Card(float amount, String type, String expiryDate, String number, String holderName, String CVV) {
        this.amount = amount;
        this.type = type;
        this.expiryDate = expiryDate;
        this.number = number;
        this.holderName = holderName;
        this.CVV = CVV;
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

    public boolean Authorize(){
        //Bank Call
        return true;
    }
}
