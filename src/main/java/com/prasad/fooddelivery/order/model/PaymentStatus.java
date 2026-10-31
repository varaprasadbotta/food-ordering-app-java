package com.prasad.fooddelivery.order.model;

import java.util.Set;

// The order's summary of "has this order been paid?".
// Individual payment attempts are tracked by payment.model.Payment.
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED;

    // FAILED -> SUCCESS: the customer retried with a new payment attempt and it went through
    public Set<PaymentStatus> allowedNextStatuses() {
        return switch (this) {
            case PENDING -> Set.of(SUCCESS, FAILED);
            case FAILED -> Set.of(SUCCESS);
            case SUCCESS -> Set.of(REFUNDED);
            case REFUNDED -> Set.of();
        };
    }

    public boolean canTransitionTo(PaymentStatus nextStatus) {
        return allowedNextStatuses().contains(nextStatus);
    }
}
