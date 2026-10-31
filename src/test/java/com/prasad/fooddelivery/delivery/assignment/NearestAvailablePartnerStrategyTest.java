package com.prasad.fooddelivery.delivery.assignment;

import com.prasad.fooddelivery.common.geo.GeoLocation;
import com.prasad.fooddelivery.delivery.model.Delivery;
import com.prasad.fooddelivery.delivery.model.DeliveryPartner;
import com.prasad.fooddelivery.delivery.model.VehicleType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NearestAvailablePartnerStrategyTest {

    // Bengaluru locations, distances from the restaurant at MG Road
    private static final GeoLocation MG_ROAD = new GeoLocation(12.9756, 77.6066);
    private static final GeoLocation INDIRANAGAR = new GeoLocation(12.9784, 77.6408);   // ~3.7 km
    private static final GeoLocation KORAMANGALA = new GeoLocation(12.9352, 77.6245);   // ~4.8 km
    private static final GeoLocation WHITEFIELD = new GeoLocation(12.9698, 77.7500);    // ~15.5 km

    private final DeliveryAssignmentStrategy strategy = new NearestAvailablePartnerStrategy(10.0);

    private final Delivery delivery = new Delivery(UUID.randomUUID(), MG_ROAD, KORAMANGALA);

    private DeliveryPartner onlinePartnerAt(GeoLocation location) {
        DeliveryPartner partner = new DeliveryPartner("Partner", "9876543210", VehicleType.BICYCLE, null);
        partner.goOnline(location);
        return partner;
    }

    @Test
    void shouldPickNearestAvailablePartner() {
        DeliveryPartner nearer = onlinePartnerAt(INDIRANAGAR);
        DeliveryPartner farther = onlinePartnerAt(KORAMANGALA);

        Optional<DeliveryPartner> selected = strategy.selectPartner(delivery, List.of(farther, nearer));

        assertEquals(nearer.getId(), selected.orElseThrow().getId());
    }

    @Test
    void shouldSkipBusyPartnerEvenIfNearer() {
        DeliveryPartner busyButNear = onlinePartnerAt(INDIRANAGAR);
        busyButNear.startDelivery();
        DeliveryPartner freeButFarther = onlinePartnerAt(KORAMANGALA);

        Optional<DeliveryPartner> selected = strategy.selectPartner(delivery, List.of(busyButNear, freeButFarther));

        assertEquals(freeButFarther.getId(), selected.orElseThrow().getId());
    }

    @Test
    void shouldSkipOfflinePartners() {
        DeliveryPartner offline = new DeliveryPartner("Partner", "9876543210", VehicleType.BICYCLE, null);

        assertTrue(strategy.selectPartner(delivery, List.of(offline)).isEmpty());
    }

    @Test
    void shouldIgnorePartnersOutsideMaxDistance() {
        DeliveryPartner tooFar = onlinePartnerAt(WHITEFIELD);

        assertTrue(strategy.selectPartner(delivery, List.of(tooFar)).isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoCandidates() {
        assertTrue(strategy.selectPartner(delivery, List.of()).isEmpty());
    }

    @Test
    void shouldRejectNonPositiveMaxDistance() {
        assertThrows(IllegalArgumentException.class, () -> new NearestAvailablePartnerStrategy(0));
    }
}
