package com.prasad.fooddelivery.payment.gateway;

// Contract for an external payment provider (Razorpay, Stripe, a bank, ...).
// PaymentService depends on this interface, never on a specific provider.
//
// Implementations must be idempotent: calling charge() or refund() again with the same
// idempotency key returns the original result instead of moving money a second time.
public interface PaymentGateway {

    GatewayResult charge(ChargeRequest request);

    GatewayResult refund(RefundRequest request);
}
