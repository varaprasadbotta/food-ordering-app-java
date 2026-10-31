package com.prasad.fooddelivery.delivery.model;

import com.prasad.fooddelivery.common.geo.GeoLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryPartnerTest {

    private static final GeoLocation MG_ROAD = new GeoLocation(12.9756, 77.6066);

    private DeliveryPartner createPartner() {
        return new DeliveryPartner("Ravi Teja", "9876543210", VehicleType.MOTORCYCLE, "KA01AB1234");
    }

    @Test
    void shouldCreatePartnerAsActiveAndOffline() {
        DeliveryPartner partner = createPartner();

        assertNotNull(partner.getId());
        assertEquals("Ravi Teja", partner.getFullName());
        assertEquals(VehicleType.MOTORCYCLE, partner.getVehicleType());
        assertTrue(partner.isActive());
        assertEquals(DeliveryPartnerStatus.OFFLINE, partner.getStatus());
        assertNull(partner.getCurrentLocation());
        assertFalse(partner.isAvailableForAssignment());
    }

    @Test
    void shouldRequireVehicleNumberForMotorVehicles() {
        assertThrows(IllegalArgumentException.class, () ->
                new DeliveryPartner("Ravi Teja", "9876543210", VehicleType.SCOOTER, null)
        );
    }

    @Test
    void shouldAllowBicycleWithoutVehicleNumber() {
        DeliveryPartner partner = new DeliveryPartner("Ravi Teja", "9876543210", VehicleType.BICYCLE, null);

        assertNull(partner.getVehicleNumber());
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class, () ->
                new DeliveryPartner(" ", "9876543210", VehicleType.BICYCLE, null)
        );
    }

    @Test
    void shouldGoOnlineWithLocation() {
        DeliveryPartner partner = createPartner();

        partner.goOnline(MG_ROAD);

        assertEquals(DeliveryPartnerStatus.AVAILABLE, partner.getStatus());
        assertEquals(MG_ROAD, partner.getCurrentLocation());
        assertTrue(partner.isAvailableForAssignment());
    }

    @Test
    void shouldRequireLocationToGoOnline() {
        DeliveryPartner partner = createPartner();

        assertThrows(IllegalArgumentException.class, () -> partner.goOnline(null));
    }

    @Test
    void shouldNotGoOnlineWhenSuspended() {
        DeliveryPartner partner = createPartner();
        partner.deactivate();

        assertThrows(IllegalStateException.class, () -> partner.goOnline(MG_ROAD));
    }

    @Test
    void shouldBecomeBusyAndAvailableAgainAroundADelivery() {
        DeliveryPartner partner = createPartner();
        partner.goOnline(MG_ROAD);

        partner.startDelivery();
        assertEquals(DeliveryPartnerStatus.BUSY, partner.getStatus());
        assertFalse(partner.isAvailableForAssignment());

        partner.finishDelivery();
        assertEquals(DeliveryPartnerStatus.AVAILABLE, partner.getStatus());
    }

    @Test
    void shouldNotStartDeliveryWhileOffline() {
        DeliveryPartner partner = createPartner();

        assertThrows(IllegalStateException.class, partner::startDelivery);
    }

    @Test
    void shouldNotGoOfflineDuringDelivery() {
        DeliveryPartner partner = createPartner();
        partner.goOnline(MG_ROAD);
        partner.startDelivery();

        assertThrows(IllegalStateException.class, partner::goOffline);
    }

    @Test
    void shouldNotBeSuspendedDuringDelivery() {
        DeliveryPartner partner = createPartner();
        partner.goOnline(MG_ROAD);
        partner.startDelivery();

        assertThrows(IllegalStateException.class, partner::deactivate);
    }

    @Test
    void shouldGoOfflineWhenSuspended() {
        DeliveryPartner partner = createPartner();
        partner.goOnline(MG_ROAD);

        partner.deactivate();

        assertFalse(partner.isActive());
        assertEquals(DeliveryPartnerStatus.OFFLINE, partner.getStatus());
    }

    @Test
    void shouldUpdateLocationWhileOnline() {
        DeliveryPartner partner = createPartner();
        partner.goOnline(MG_ROAD);
        GeoLocation koramangala = new GeoLocation(12.9352, 77.6245);

        partner.updateLocation(koramangala);

        assertEquals(koramangala, partner.getCurrentLocation());
    }

    @Test
    void shouldNotUpdateLocationWhileOffline() {
        DeliveryPartner partner = createPartner();

        assertThrows(IllegalStateException.class, () -> partner.updateLocation(MG_ROAD));
    }
}
