package com.prasad.fooddelivery.delivery.model;

import com.prasad.fooddelivery.common.geo.GeoLocation;

import java.time.Instant;
import java.util.UUID;

// The delivery job for one order: pick up at the restaurant, drop at the customer.
public class Delivery {

    private final UUID id;
    private final UUID orderId;

    // Copied when the delivery is created, so the job doesn't change if the
    // customer edits their saved address or the restaurant moves later.
    private final GeoLocation pickupLocation;
    private final GeoLocation dropLocation;

    private DeliveryStatus status;
    private UUID deliveryPartnerId;

    private Instant assignedAt;
    private Instant pickedUpAt;
    private Instant deliveredAt;

    private final Instant createdAt;
    private Instant updatedAt;

    public Delivery(UUID orderId, GeoLocation pickupLocation, GeoLocation dropLocation) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is required");
        }

        if (pickupLocation == null) {
            throw new IllegalArgumentException("Pickup location is required");
        }

        if (dropLocation == null) {
            throw new IllegalArgumentException("Drop location is required");
        }

        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;

        this.status = DeliveryStatus.PENDING_ASSIGNMENT;

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    // Used only by restore() to rebuild an existing delivery
    private Delivery(UUID id, UUID orderId, GeoLocation pickupLocation, GeoLocation dropLocation,
                     DeliveryStatus status, UUID deliveryPartnerId,
                     Instant assignedAt, Instant pickedUpAt, Instant deliveredAt,
                     Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.status = status;
        this.deliveryPartnerId = deliveryPartnerId;
        this.assignedAt = assignedAt;
        this.pickedUpAt = pickedUpAt;
        this.deliveredAt = deliveredAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Rebuilds a delivery that already exists (e.g. loaded from the database)
    public static Delivery restore(UUID id, UUID orderId,
                                   GeoLocation pickupLocation, GeoLocation dropLocation,
                                   DeliveryStatus status, UUID deliveryPartnerId,
                                   Instant assignedAt, Instant pickedUpAt, Instant deliveredAt,
                                   Instant createdAt, Instant updatedAt) {
        if (id == null) {
            throw new IllegalArgumentException("Delivery ID is required");
        }

        if (status == null) {
            throw new IllegalArgumentException("Delivery status is required");
        }

        return new Delivery(id, orderId, pickupLocation, dropLocation, status, deliveryPartnerId,
                assignedAt, pickedUpAt, deliveredAt, createdAt, updatedAt);
    }

    public void assignTo(UUID deliveryPartnerId) {
        if (deliveryPartnerId == null) {
            throw new IllegalArgumentException("Delivery partner ID is required");
        }

        changeStatus(DeliveryStatus.ASSIGNED);
        this.deliveryPartnerId = deliveryPartnerId;
        this.assignedAt = this.updatedAt;
    }

    public void accept(UUID deliveryPartnerId) {
        requireAssignedPartner(deliveryPartnerId);
        changeStatus(DeliveryStatus.ACCEPTED);
    }

    // Goes back to the pool so the assignment service can offer it to someone else
    public void reject(UUID deliveryPartnerId) {
        requireAssignedPartner(deliveryPartnerId);
        changeStatus(DeliveryStatus.PENDING_ASSIGNMENT);
        this.deliveryPartnerId = null;
        this.assignedAt = null;
    }

    public void markPickedUp(UUID deliveryPartnerId) {
        requireAssignedPartner(deliveryPartnerId);
        changeStatus(DeliveryStatus.PICKED_UP);
        this.pickedUpAt = this.updatedAt;
    }

    public void markDelivered(UUID deliveryPartnerId) {
        requireAssignedPartner(deliveryPartnerId);
        changeStatus(DeliveryStatus.DELIVERED);
        this.deliveredAt = this.updatedAt;
    }

    public void cancel() {
        changeStatus(DeliveryStatus.CANCELLED);
    }

    // Only the partner the delivery is assigned to may act on it
    private void requireAssignedPartner(UUID deliveryPartnerId) {
        if (deliveryPartnerId == null || !deliveryPartnerId.equals(this.deliveryPartnerId)) {
            throw new IllegalStateException("Delivery is not assigned to this delivery partner");
        }
    }

    private void changeStatus(DeliveryStatus nextStatus) {
        if (!status.canTransitionTo(nextStatus)) {
            throw new IllegalStateException(
                    "Cannot change delivery status from " + status + " to " + nextStatus
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

    public GeoLocation getPickupLocation() {
        return pickupLocation;
    }

    public GeoLocation getDropLocation() {
        return dropLocation;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public UUID getDeliveryPartnerId() {
        return deliveryPartnerId;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public Instant getPickedUpAt() {
        return pickedUpAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
