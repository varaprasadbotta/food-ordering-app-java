package com.prasad.fooddelivery.payment.gateway;

import com.prasad.fooddelivery.payment.model.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MockPaymentGatewayTest {

    private ChargeRequest chargeRequest(String idempotencyKey) {
        return new ChargeRequest(idempotencyKey, new BigDecimal("557.00"), PaymentMethod.UPI);
    }

    @Test
    void shouldApproveCharge() {
        MockPaymentGateway gateway = MockPaymentGateway.alwaysApproving();

        GatewayResult result = gateway.charge(chargeRequest("key-1"));

        assertTrue(result.successful());
        assertNotNull(result.transactionId());
        assertNull(result.failureReason());
    }

    @Test
    void shouldDeclineCharge() {
        MockPaymentGateway gateway = MockPaymentGateway.alwaysDeclining("Insufficient balance");

        GatewayResult result = gateway.charge(chargeRequest("key-1"));

        assertFalse(result.successful());
        assertNull(result.transactionId());
        assertEquals("Insufficient balance", result.failureReason());
    }

    @Test
    void shouldChargeOnlyOnceForSameIdempotencyKey() {
        MockPaymentGateway gateway = MockPaymentGateway.alwaysApproving();

        // e.g. the customer double-clicks "Pay", or our request times out and we retry
        GatewayResult first = gateway.charge(chargeRequest("key-1"));
        GatewayResult retry = gateway.charge(chargeRequest("key-1"));

        assertEquals(first, retry);
        assertEquals(1, gateway.chargeCount());
    }

    @Test
    void shouldChargeAgainForDifferentIdempotencyKey() {
        MockPaymentGateway gateway = MockPaymentGateway.alwaysApproving();

        gateway.charge(chargeRequest("key-1"));
        gateway.charge(chargeRequest("key-2"));

        assertEquals(2, gateway.chargeCount());
    }

    @Test
    void shouldRefundEarlierCharge() {
        MockPaymentGateway gateway = MockPaymentGateway.alwaysApproving();
        GatewayResult charge = gateway.charge(chargeRequest("key-1"));

        GatewayResult refund = gateway.refund(
                new RefundRequest("refund-1", charge.transactionId(), new BigDecimal("557.00"))
        );

        assertTrue(refund.successful());
    }

    @Test
    void shouldRejectRefundForUnknownTransaction() {
        MockPaymentGateway gateway = MockPaymentGateway.alwaysApproving();

        GatewayResult refund = gateway.refund(
                new RefundRequest("refund-1", "txn_does_not_exist", new BigDecimal("100"))
        );

        assertFalse(refund.successful());
    }

    @Test
    void shouldRejectRefundAboveChargedAmount() {
        MockPaymentGateway gateway = MockPaymentGateway.alwaysApproving();
        GatewayResult charge = gateway.charge(chargeRequest("key-1"));

        GatewayResult refund = gateway.refund(
                new RefundRequest("refund-1", charge.transactionId(), new BigDecimal("1000.00"))
        );

        assertFalse(refund.successful());
    }

    @Test
    void shouldNotAcceptCashOnDeliveryCharge() {
        assertThrows(IllegalArgumentException.class, () ->
                new ChargeRequest("key-1", new BigDecimal("100"), PaymentMethod.CASH_ON_DELIVERY)
        );
    }
}
