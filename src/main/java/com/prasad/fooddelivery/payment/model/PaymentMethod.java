package com.prasad.fooddelivery.payment.model;

public enum PaymentMethod {
    UPI,
    CARD,
    NET_BANKING,
    WALLET,
    CASH_ON_DELIVERY;

    // Online methods are charged through a payment gateway before the order is prepared.
    // Cash on delivery is collected by the delivery partner instead.
    public boolean isOnline() {
        return this != CASH_ON_DELIVERY;
    }
}
