package com.prasad.fooddelivery.delivery.model;

import com.prasad.fooddelivery.common.geo.GeoLocation;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryTest {

    private static final GeoLocation RESTAURANT = new GeoLocation(12.9756, 77.6066);
    private static final GeoLocation CUSTOMER = new GeoLocation(12.9352, 77.6245);

    private final UUID partnerId = UUID.randomUUID();

    private Delivery createDelivery() {
        return new Delivery(UUID.randomUUID(), RESTAURANT, CUSTOMER);
    }

    @Test
    void shouldCreateDeliveryPendingAssignment() {
        Delivery delivery = createDelivery();

        assertNotNull(delivery.getId());
        assertNotNull(delivery.getOrderId());
        assertEquals(RESTAURANT, delivery.getPickupLocation());
        assertEquals(CUSTOMER, delivery.getDropLocation());
        assertEquals(DeliveryStatus.PENDING_ASSIGNMENT, delivery.getStatus());
        assertNull(delivery.getDeliveryPartnerId());
    }

    @Test
    void shouldRejectMissingLocations() {
        assertThrows(IllegalArgumentException.class, () ->
                new Delivery(UUID.randomUUID(), null, CUSTOMER)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Delivery(UUID.randomUUID(), RESTAURANT, null)
        );
    }

    @Test
    void shouldCompleteFullDeliveryLifecycle() {
        Delivery delivery = createDelivery();

        delivery.assignTo(partnerId);
        delivery.accept(partnerId);
        delivery.markPickedUp(partnerId);
        delivery.markDelivered(partnerId);

        assertEquals(DeliveryStatus.DELIVERED, delivery.getStatus());
        assertEquals(partnerId, delivery.getDeliveryPartnerId());
        assertNotNull(delivery.getAssignedAt());
        assertNotNull(delivery.getPickedUpAt());
        assertNotNull(delivery.getDeliveredAt());
    }

    @Test
    void shouldReturnToPoolWhenPartnerRejects() {
        Delivery delivery = createDelivery();
        delivery.assignTo(partnerId);

        delivery.reject(partnerId);

        assertEquals(DeliveryStatus.PENDING_ASSIGNMENT, delivery.getStatus());
        assertNull(delivery.getDeliveryPartnerId());
        assertNull(delivery.getAssignedAt());
    }

    @Test
    void shouldBeReassignableAfterRejection() {
        Delivery delivery = createDelivery();
        delivery.assignTo(partnerId);
        delivery.reject(partnerId);
        UUID secondPartnerId = UUID.randomUUID();

        delivery.assignTo(secondPartnerId);

        assertEquals(secondPartnerId, delivery.getDeliveryPartnerId());
    }

    @Test
    void shouldNotLetAnotherPartnerAccept() {
        Delivery delivery = createDelivery();
        delivery.assignTo(partnerId);

        assertThrows(IllegalStateException.class, () -> delivery.accept(UUID.randomUUID()));
        assertEquals(DeliveryStatus.ASSIGNED, delivery.getStatus());
    }

    @Test
    void shouldNotAssignTwice() {
        Delivery delivery = createDelivery();
        delivery.assignTo(partnerId);

        assertThrows(IllegalStateException.class, () -> delivery.assignTo(UUID.randomUUID()));
        assertEquals(partnerId, delivery.getDeliveryPartnerId());
    }

    @Test
    void shouldNotPickUpBeforeAccepting() {
        Delivery delivery = createDelivery();
        delivery.assignTo(partnerId);

        assertThrows(IllegalStateException.class, () -> delivery.markPickedUp(partnerId));
    }

    @Test
    void shouldAllowCancellationBeforePickup() {
        Delivery delivery = createDelivery();
        delivery.assignTo(partnerId);
        delivery.accept(partnerId);

        delivery.cancel();

        assertEquals(DeliveryStatus.CANCELLED, delivery.getStatus());
    }

    @Test
    void shouldNotCancelAfterPickup() {
        Delivery delivery = createDelivery();
        delivery.assignTo(partnerId);
        delivery.accept(partnerId);
        delivery.markPickedUp(partnerId);

        assertThrows(IllegalStateException.class, delivery::cancel);
    }
}
