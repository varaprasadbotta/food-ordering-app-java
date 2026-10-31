package com.prasad.fooddelivery.payment.gateway;

import com.prasad.fooddelivery.payment.model.PaymentMethod;

import java.math.BigDecimal;

// Immutable request sent to a payment gateway
public record ChargeRequest(String idempotencyKey, BigDecimal amount, PaymentMethod method) {

    public ChargeRequest {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency key is required");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (method == null || !method.isOnline()) {
            throw new IllegalArgumentException("An online payment method is required");
        }
    }
}
