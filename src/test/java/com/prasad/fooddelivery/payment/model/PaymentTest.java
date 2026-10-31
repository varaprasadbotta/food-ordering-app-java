package com.prasad.fooddelivery.payment.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Payment createPayment() {
        return new Payment(
                UUID.randomUUID(),
                new BigDecimal("557.00"),
                PaymentMethod.UPI,
                "pay_" + UUID.randomUUID()
        );
    }

    @Test
    void shouldCreatePaymentAsInitiated() {
        Payment payment = createPayment();

        assertNotNull(payment.getId());
        assertNotNull(payment.getOrderId());
        assertEquals(new BigDecimal("557.00"), payment.getAmount());
        assertEquals(PaymentMethod.UPI, payment.getMethod());
        assertEquals(TransactionStatus.INITIATED, payment.getStatus());
        assertNull(payment.getGatewayTransactionId());
        assertNull(payment.getFailureReason());
        assertNotNull(payment.getCreatedAt());
    }

    @Test
    void shouldRejectNullOrderId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Payment(null, new BigDecimal("100"), PaymentMethod.UPI, "key")
        );
    }

    @Test
    void shouldRejectZeroAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                new Payment(UUID.randomUUID(), BigDecimal.ZERO, PaymentMethod.UPI, "key")
        );
    }

    @Test
    void shouldRejectMissingMethod() {
        assertThrows(IllegalArgumentException.class, () ->
                new Payment(UUID.randomUUID(), new BigDecimal("100"), null, "key")
        );
    }

    @Test
    void shouldRejectBlankIdempotencyKey() {
        assertThrows(IllegalArgumentException.class, () ->
                new Payment(UUID.randomUUID(), new BigDecimal("100"), PaymentMethod.UPI, " ")
        );
    }

    @Test
    void shouldMarkSucceededWithGatewayTransactionId() {
        Payment payment = createPayment();

        payment.markSucceeded("txn_123");

        assertEquals(TransactionStatus.SUCCEEDED, payment.getStatus());
        assertEquals("txn_123", payment.getGatewayTransactionId());
    }

    @Test
    void shouldRequireGatewayTransactionIdOnSuccess() {
        Payment payment = createPayment();

        assertThrows(IllegalArgumentException.class, () -> payment.markSucceeded(null));
        assertEquals(TransactionStatus.INITIATED, payment.getStatus());
    }

    @Test
    void shouldMarkFailedWithReason() {
        Payment payment = createPayment();

        payment.markFailed("Insufficient balance");

        assertEquals(TransactionStatus.FAILED, payment.getStatus());
        assertEquals("Insufficient balance", payment.getFailureReason());
    }

    @Test
    void shouldRefundSucceededPayment() {
        Payment payment = createPayment();
        payment.markSucceeded("txn_123");

        payment.markRefunded();

        assertEquals(TransactionStatus.REFUNDED, payment.getStatus());
    }

    @Test
    void shouldNotRefundPaymentThatNeverSucceeded() {
        Payment payment = createPayment();

        assertThrows(IllegalStateException.class, payment::markRefunded);
    }

    @Test
    void shouldNotRetryFailedPaymentInPlace() {
        Payment payment = createPayment();
        payment.markFailed("Declined");

        assertThrows(IllegalStateException.class, () -> payment.markSucceeded("txn_123"));
    }

    @Test
    void shouldNotSucceedTwice() {
        Payment payment = createPayment();
        payment.markSucceeded("txn_123");

        assertThrows(IllegalStateException.class, () -> payment.markSucceeded("txn_456"));
        assertEquals("txn_123", payment.getGatewayTransactionId());
    }

    @Test
    void shouldRestoreExistingPayment() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-10T10:15:30Z");

        Payment payment = Payment.restore(id, UUID.randomUUID(), new BigDecimal("557.00"),
                PaymentMethod.CARD, "key", TransactionStatus.SUCCEEDED, "txn_123", null,
                createdAt, createdAt);

        assertEquals(id, payment.getId());
        assertEquals(TransactionStatus.SUCCEEDED, payment.getStatus());
        assertEquals("txn_123", payment.getGatewayTransactionId());
        assertEquals(createdAt, payment.getCreatedAt());
    }
}
