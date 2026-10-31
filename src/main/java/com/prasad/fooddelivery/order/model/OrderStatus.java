package com.prasad.fooddelivery.order.model;

import java.util.Set;

public enum OrderStatus {
    PLACED,
    CONFIRMED,
    PREPARING,
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;

    // The statuses an order may move to from this one.
    // Cancellation is allowed only before the restaurant starts preparing.
    public Set<OrderStatus> allowedNextStatuses() {
        return switch (this) {
            case PLACED -> Set.of(CONFIRMED, CANCELLED);
            case CONFIRMED -> Set.of(PREPARING, CANCELLED);
            case PREPARING -> Set.of(READY_FOR_PICKUP);
            case READY_FOR_PICKUP -> Set.of(OUT_FOR_DELIVERY);
            case OUT_FOR_DELIVERY -> Set.of(DELIVERED);
            case DELIVERED, CANCELLED -> Set.of();
        };
    }

    public boolean canTransitionTo(OrderStatus nextStatus) {
        return allowedNextStatuses().contains(nextStatus);
    }

    public boolean isTerminal() {
        return allowedNextStatuses().isEmpty();
    }
}
