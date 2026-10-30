package com.prasad.fooddelivery.customer.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private Address createAddress() {
        return new Address(
                UUID.randomUUID(),
                AddressLabel.HOME,
                "Flat 101",
                "MG Road",
                "Near Metro Station",
                "Bengaluru",
                "Karnataka",
                "560001",
                new BigDecimal("12.9716"),
                new BigDecimal("77.5946")
        );
    }

    @Test
    void shouldCreateAddress() {
        Address address = createAddress();

        assertNotNull(address.getId());
        assertNotNull(address.getCustomerId());
        assertEquals(AddressLabel.HOME, address.getLabel());
        assertEquals("Bengaluru", address.getCity());
        assertEquals("Karnataka", address.getState());
        assertEquals("560001", address.getPostalCode());
        assertFalse(address.isDefaultAddress());
        assertNotNull(address.getCreatedAt());
        assertNotNull(address.getUpdatedAt());
    }

    @Test
    void shouldRejectNullCustomerId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Address(
                        null,
                        AddressLabel.HOME,
                        "Flat 101",
                        "MG Road",
                        null,
                        "Bengaluru",
                        "Karnataka",
                        "560001",
                        null,
                        null
                )
        );
    }

    @Test
    void shouldRejectNullAddressLabel() {
        assertThrows(IllegalArgumentException.class, () ->
                new Address(
                        UUID.randomUUID(),
                        null,
                        "Flat 101",
                        "MG Road",
                        null,
                        "Bengaluru",
                        "Karnataka",
                        "560001",
                        null,
                        null
                )
        );
    }

    @Test
    void shouldRejectBlankCity() {
        assertThrows(IllegalArgumentException.class, () ->
                new Address(
                        UUID.randomUUID(),
                        AddressLabel.HOME,
                        "Flat 101",
                        "MG Road",
                        null,
                        " ",
                        "Karnataka",
                        "560001",
                        null,
                        null
                )
        );
    }

    @Test
    void shouldRejectBlankState() {
        assertThrows(IllegalArgumentException.class, () ->
                new Address(
                        UUID.randomUUID(),
                        AddressLabel.HOME,
                        "Flat 101",
                        "MG Road",
                        null,
                        "Bengaluru",
                        "",
                        "560001",
                        null,
                        null
                )
        );
    }

    @Test
    void shouldRejectBlankPostalCode() {
        assertThrows(IllegalArgumentException.class, () ->
                new Address(
                        UUID.randomUUID(),
                        AddressLabel.HOME,
                        "Flat 101",
                        "MG Road",
                        null,
                        "Bengaluru",
                        "Karnataka",
                        " ",
                        null,
                        null
                )
        );
    }

    @Test
    void shouldAllowOptionalCoordinates() {
        Address address = new Address(
                UUID.randomUUID(),
                AddressLabel.WORK,
                "Office 201",
                "Outer Ring Road",
                null,
                "Bengaluru",
                "Karnataka",
                "560103",
                null,
                null
        );

        assertNull(address.getLatitude());
        assertNull(address.getLongitude());
    }

    @Test
    void shouldUpdateAddress() {
        Address address = createAddress();

        address.updateAddress(
                AddressLabel.WORK,
                "Office 202",
                "Outer Ring Road",
                "Near Tech Park",
                "Bengaluru",
                "Karnataka",
                "560103",
                new BigDecimal("12.9352"),
                new BigDecimal("77.6245")
        );

        assertEquals(AddressLabel.WORK, address.getLabel());
        assertEquals("Office 202", address.getHouseNumber());
        assertEquals("Outer Ring Road", address.getStreet());
        assertEquals("560103", address.getPostalCode());
        assertEquals(new BigDecimal("12.9352"), address.getLatitude());
        assertEquals(new BigDecimal("77.6245"), address.getLongitude());
    }

    @Test
    void shouldRejectInvalidCityDuringUpdate() {
        Address address = createAddress();

        assertThrows(IllegalArgumentException.class, () ->
                address.updateAddress(
                        AddressLabel.HOME,
                        "Flat 101",
                        "MG Road",
                        null,
                        " ",
                        "Karnataka",
                        "560001",
                        null,
                        null
                )
        );

        // Existing address should remain unchanged.
        assertEquals("Bengaluru", address.getCity());
    }

    @Test
    void shouldSetAddressAsDefault() {
        Address address = createAddress();

        address.setAsDefault();

        assertTrue(address.isDefaultAddress());
    }

    @Test
    void shouldRemoveDefaultStatus() {
        Address address = createAddress();

        address.setAsDefault();
        address.removeAsDefault();

        assertFalse(address.isDefaultAddress());
    }
}