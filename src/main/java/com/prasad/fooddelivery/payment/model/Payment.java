package com.prasad.fooddelivery.payment.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// One payment attempt for an order.
public class Payment {

    private final UUID id;
    private final UUID orderId;
    private final BigDecimal amount;
    private final PaymentMethod method;

    // Unique per attempt. Sent to the gateway so that a retried request
    // (network timeout, double click) is never charged twice.
    private final String idempotencyKey;

    private TransactionStatus status;
    private String gatewayTransactionId;
    private String failureReason;

    private final Instant createdAt;
    private Instant updatedAt;

    public Payment(UUID orderId, BigDecimal amount, PaymentMethod method, String idempotencyKey) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is required");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (method == null) {
            throw new IllegalArgumentException("Payment method is required");
        }

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency key is required");
        }

        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.idempotencyKey = idempotencyKey;

        this.status = TransactionStatus.INITIATED;

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    // Used only by restore() to rebuild an existing payment
    private Payment(
            UUID id,
            UUID orderId,
            BigDecimal amount,
            PaymentMethod method,
            String idempotencyKey,
            TransactionStatus status,
            String gatewayTransactionId,
            String failureReason,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.idempotencyKey = idempotencyKey;
        this.status = status;
        this.gatewayTransactionId = gatewayTransactionId;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Rebuilds a payment that already exists (e.g. loaded from the database)
    public static Payment restore(
            UUID id,
            UUID orderId,
            BigDecimal amount,
            PaymentMethod method,
            String idempotencyKey,
            TransactionStatus status,
            String gatewayTransactionId,
            String failureReason,
            Instant createdAt,
            Instant updatedAt
    ) {
        if (id == null) {
            throw new IllegalArgumentException("Payment ID is required");
        }

        if (status == null) {
            throw new IllegalArgumentException("Transaction status is required");
        }

        return new Payment(id, orderId, amount, method, idempotencyKey, status,
                gatewayTransactionId, failureReason, createdAt, updatedAt);
    }

    public void markSucceeded(String gatewayTransactionId) {
        if (gatewayTransactionId == null || gatewayTransactionId.isBlank()) {
            throw new IllegalArgumentException("Gateway transaction ID is required");
        }

        changeStatus(TransactionStatus.SUCCEEDED);
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public void markFailed(String failureReason) {
        changeStatus(TransactionStatus.FAILED);
        this.failureReason = failureReason;
    }

    public void markRefunded() {
        changeStatus(TransactionStatus.REFUNDED);
    }

    private void changeStatus(TransactionStatus nextStatus) {
        if (!status.canTransitionTo(nextStatus)) {
            throw new IllegalStateException(
                    "Cannot change payment status from " + status + " to " + nextStatus
            );
        }

        this.status = nextStatus;
        this.updatedAt = Instant.now();
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
