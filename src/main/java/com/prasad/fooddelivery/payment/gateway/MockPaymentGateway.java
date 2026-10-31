package com.prasad.fooddelivery.payment.gateway;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// In-process stand-in for a real gateway, for local runs and tests.
// It behaves like a real one in the ways that matter to our code:
// idempotent by key, and refunds only allowed for charges it actually made.
public class MockPaymentGateway implements PaymentGateway {

    private final String declineReason;

    private final Map<String, GatewayResult> resultsByIdempotencyKey = new ConcurrentHashMap<>();
    private final Map<String, BigDecimal> chargedAmountByTransactionId = new ConcurrentHashMap<>();

    private MockPaymentGateway(String declineReason) {
        this.declineReason = declineReason;
    }

    public static MockPaymentGateway alwaysApproving() {
        return new MockPaymentGateway(null);
    }

    public static MockPaymentGateway alwaysDeclining(String declineReason) {
        return new MockPaymentGateway(declineReason);
    }

    @Override
    public GatewayResult charge(ChargeRequest request) {
        // computeIfAbsent runs the charge at most once per key, even under concurrent retries
        return resultsByIdempotencyKey.computeIfAbsent(request.idempotencyKey(), key -> {
            if (declineReason != null) {
                return GatewayResult.failure(declineReason);
            }

            String transactionId = "mock_txn_" + UUID.randomUUID();
            chargedAmountByTransactionId.put(transactionId, request.amount());
            return GatewayResult.success(transactionId);
        });
    }

    @Override
    public GatewayResult refund(RefundRequest request) {
        return resultsByIdempotencyKey.computeIfAbsent(request.idempotencyKey(), key -> {
            BigDecimal chargedAmount = chargedAmountByTransactionId.get(request.gatewayTransactionId());

            if (chargedAmount == null) {
                return GatewayResult.failure("Unknown transaction " + request.gatewayTransactionId());
            }

            if (request.amount().compareTo(chargedAmount) > 0) {
                return GatewayResult.failure("Refund exceeds charged amount");
            }

            return GatewayResult.success("mock_refund_" + UUID.randomUUID());
        });
    }

    // Lets tests check that retries did not create extra charges
    public int chargeCount() {
        return chargedAmountByTransactionId.size();
    }
}
