package com.prasad.fooddelivery.payment.model;

import java.util.Set;

// Status of one payment attempt. Deliberately separate from order.model.PaymentStatus:
// an order has one "is it paid?" status, but may have several payment attempts
// (e.g. a failed UPI attempt followed by a successful card payment).
public enum TransactionStatus {
    INITIATED,
    SUCCEEDED,
    FAILED,
    REFUNDED;

    // A failed attempt is never retried in place - a retry is a new Payment.
    public Set<TransactionStatus> allowedNextStatuses() {
        return switch (this) {
            case INITIATED -> Set.of(SUCCEEDED, FAILED);
            case SUCCEEDED -> Set.of(REFUNDED);
            case FAILED, REFUNDED -> Set.of();
        };
    }

    public boolean canTransitionTo(TransactionStatus nextStatus) {
        return allowedNextStatuses().contains(nextStatus);
    }
}
