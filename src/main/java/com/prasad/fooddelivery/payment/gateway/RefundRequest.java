package com.prasad.fooddelivery.payment.gateway;

import java.math.BigDecimal;

// Immutable refund request for an earlier successful charge
public record RefundRequest(String idempotencyKey, String gatewayTransactionId, BigDecimal amount) {

    public RefundRequest {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency key is required");
        }

        if (gatewayTransactionId == null || gatewayTransactionId.isBlank()) {
            throw new IllegalArgumentException("Gateway transaction ID is required");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
}
