package com.unipi.prospect.commerce;

import java.util.Objects;

public class Payment {
    private float amount;

    public Payment(float amount) {
        this.amount = amount;
    }

    public void pay(IPayment paymentMethod) {
        if (paymentMethod != null) {
            paymentMethod.pay(amount);
        } else {
            System.out.println("Payment method is not specified.");
        }
    }




}
