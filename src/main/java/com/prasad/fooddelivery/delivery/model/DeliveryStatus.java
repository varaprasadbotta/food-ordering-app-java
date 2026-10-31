package com.prasad.fooddelivery.delivery.model;

import java.util.Set;

public enum DeliveryStatus {
    PENDING_ASSIGNMENT,
    ASSIGNED,
    ACCEPTED,
    PICKED_UP,
    DELIVERED,
    CANCELLED;

    // ASSIGNED -> PENDING_ASSIGNMENT: the partner rejected it, so it goes back to be reassigned.
    // Cancellation is allowed until the food has been picked up.
    public Set<DeliveryStatus> allowedNextStatuses() {
        return switch (this) {
            case PENDING_ASSIGNMENT -> Set.of(ASSIGNED, CANCELLED);
            case ASSIGNED -> Set.of(ACCEPTED, PENDING_ASSIGNMENT, CANCELLED);
            case ACCEPTED -> Set.of(PICKED_UP, CANCELLED);
            case PICKED_UP -> Set.of(DELIVERED);
            case DELIVERED, CANCELLED -> Set.of();
        };
    }

    public boolean canTransitionTo(DeliveryStatus nextStatus) {
        return allowedNextStatuses().contains(nextStatus);
    }
}
