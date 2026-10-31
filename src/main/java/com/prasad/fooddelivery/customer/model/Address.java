package com.prasad.fooddelivery.customer.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Address {

    private final UUID id;
    private final UUID customerId;

    private AddressLabel label;
    private String houseNumber;
    private String street;
    private String landmark;
    private String city;
    private String state;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;

    private boolean defaultAddress;

    private final Instant createdAt;
    private Instant updatedAt;

    public Address(
            UUID customerId,
            AddressLabel label,
            String houseNumber,
            String street,
            String landmark,
            String city,
            String state,
            String postalCode,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        if (label == null) {
            throw new IllegalArgumentException("Address label is required");
        }

        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City is required");
        }

        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State is required");
        }

        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException("Postal code is required");
        }

        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.label = label;
        this.houseNumber = houseNumber;
        this.street = street;
        this.landmark = landmark;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;

        this.defaultAddress = false;

        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Used only by restore() to rebuild an existing address
    private Address(
            UUID id,
            UUID customerId,
            AddressLabel label,
            String houseNumber,
            String street,
            String landmark,
            String city,
            String state,
            String postalCode,
            BigDecimal latitude,
            BigDecimal longitude,
            boolean defaultAddress,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.label = label;
        this.houseNumber = houseNumber;
        this.street = street;
        this.landmark = landmark;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.defaultAddress = defaultAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Rebuilds an address that already exists (e.g. loaded from the database)
    public static Address restore(
            UUID id,
            UUID customerId,
            AddressLabel label,
            String houseNumber,
            String street,
            String landmark,
            String city,
            String state,
            String postalCode,
            BigDecimal latitude,
            BigDecimal longitude,
            boolean defaultAddress,
            Instant createdAt,
            Instant updatedAt
    ) {
        if (id == null) {
            throw new IllegalArgumentException("Address ID is required");
        }

        return new Address(id, customerId, label, houseNumber, street, landmark,
                city, state, postalCode, latitude, longitude, defaultAddress,
                createdAt, updatedAt);
    }

    // Update address details
    public void updateAddress(
            AddressLabel label,
            String houseNumber,
            String street,
            String landmark,
            String city,
            String state,
            String postalCode,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        if (label == null) {
            throw new IllegalArgumentException("Address label is required");
        }

        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City is required");
        }

        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State is required");
        }

        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException("Postal code is required");
        }

        this.label = label;
        this.houseNumber = houseNumber;
        this.street = street;
        this.landmark = landmark;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;

        this.updatedAt = Instant.now();
    }

    // Mark this address as default
    public void setAsDefault() {
        this.defaultAddress = true;
        this.updatedAt = Instant.now();
    }

    // Remove default status
    public void removeAsDefault() {
        this.defaultAddress = false;
        this.updatedAt = Instant.now();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public AddressLabel getLabel() {
        return label;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getStreet() {
        return street;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}