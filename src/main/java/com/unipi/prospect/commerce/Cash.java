package com.unipi.prospect.commerce;

public class Cash implements IPayment{
     public float amount;

    public Cash(float amount) {
        this.amount = amount;
    }

    @Override
    public void pay(float amount) {
        System.out.println("Paying with cash: " + amount + "€");
    }
}
