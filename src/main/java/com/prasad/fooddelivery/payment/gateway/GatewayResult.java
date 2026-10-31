package com.prasad.fooddelivery.payment.gateway;

// Outcome of a gateway call. A declined card is a normal result, not an exception;
// exceptions are for "we could not reach the gateway at all".
public record GatewayResult(boolean successful, String transactionId, String failureReason) {

    public static GatewayResult success(String transactionId) {
        return new GatewayResult(true, transactionId, null);
    }

    public static GatewayResult failure(String failureReason) {
        return new GatewayResult(false, null, failureReason);
    }
}
