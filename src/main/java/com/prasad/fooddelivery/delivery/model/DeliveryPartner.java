package com.prasad.fooddelivery.delivery.model;

import com.prasad.fooddelivery.common.geo.GeoLocation;

import java.time.Instant;
import java.util.UUID;

public class DeliveryPartner {

    private final UUID id;
    private String fullName;
    private String phoneNumber;
    private VehicleType vehicleType;
    private String vehicleNumber;

    // active = account allowed to work (admin can suspend); status = what they are doing right now
    private boolean active;
    private DeliveryPartnerStatus status;
    private GeoLocation currentLocation;

    private final Instant createdAt;
    private Instant updatedAt;

    public DeliveryPartner(String fullName, String phoneNumber,
                           VehicleType vehicleType, String vehicleNumber) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type is required");
        }

        if (vehicleType != VehicleType.BICYCLE && (vehicleNumber == null || vehicleNumber.isBlank())) {
            throw new IllegalArgumentException("Vehicle number is required for motor vehicles");
        }

        this.id = UUID.randomUUID();
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;

        this.active = true;
        this.status = DeliveryPartnerStatus.OFFLINE;

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    // Used only by restore() to rebuild an existing delivery partner
    private DeliveryPartner(UUID id, String fullName, String phoneNumber,
                            VehicleType vehicleType, String vehicleNumber,
                            boolean active, DeliveryPartnerStatus status, GeoLocation currentLocation,
                            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.active = active;
        this.status = status;
        this.currentLocation = currentLocation;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Rebuilds a delivery partner that already exists (e.g. loaded from the database)
    public static DeliveryPartner restore(UUID id, String fullName, String phoneNumber,
                                          VehicleType vehicleType, String vehicleNumber,
                                          boolean active, DeliveryPartnerStatus status,
                                          GeoLocation currentLocation,
                                          Instant createdAt, Instant updatedAt) {
        if (id == null) {
            throw new IllegalArgumentException("Delivery partner ID is required");
        }

        if (status == null) {
            throw new IllegalArgumentException("Delivery partner status is required");
        }

        return new DeliveryPartner(id, fullName, phoneNumber, vehicleType, vehicleNumber,
                active, status, currentLocation, createdAt, updatedAt);
    }

    public void goOnline(GeoLocation location) {
        if (!active) {
            throw new IllegalStateException("Suspended delivery partner cannot go online");
        }

        if (status != DeliveryPartnerStatus.OFFLINE) {
            throw new IllegalStateException("Delivery partner is already online");
        }

        if (location == null) {
            throw new IllegalArgumentException("Location is required to go online");
        }

        this.currentLocation = location;
        this.status = DeliveryPartnerStatus.AVAILABLE;
        this.updatedAt = Instant.now();
    }

    public void goOffline() {
        if (status == DeliveryPartnerStatus.BUSY) {
            throw new IllegalStateException("Cannot go offline during an active delivery");
        }

        this.status = DeliveryPartnerStatus.OFFLINE;
        this.updatedAt = Instant.now();
    }

    public void updateLocation(GeoLocation location) {
        if (location == null) {
            throw new IllegalArgumentException("Location is required");
        }

        if (status == DeliveryPartnerStatus.OFFLINE) {
            throw new IllegalStateException("Offline delivery partner does not share location");
        }

        this.currentLocation = location;
        this.updatedAt = Instant.now();
    }

    // Called when the partner accepts a delivery
    public void startDelivery() {
        if (status != DeliveryPartnerStatus.AVAILABLE) {
            throw new IllegalStateException("Only an available delivery partner can start a delivery");
        }

        this.status = DeliveryPartnerStatus.BUSY;
        this.updatedAt = Instant.now();
    }

    // Called when the delivery is completed or cancelled
    public void finishDelivery() {
        if (status != DeliveryPartnerStatus.BUSY) {
            throw new IllegalStateException("Delivery partner has no active delivery");
        }

        this.status = DeliveryPartnerStatus.AVAILABLE;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    // Admin suspension. An ongoing delivery must be reassigned first.
    public void deactivate() {
        if (status == DeliveryPartnerStatus.BUSY) {
            throw new IllegalStateException("Cannot suspend a delivery partner during an active delivery");
        }

        this.active = false;
        this.status = DeliveryPartnerStatus.OFFLINE;
        this.updatedAt = Instant.now();
    }

    public boolean isAvailableForAssignment() {
        return active && status == DeliveryPartnerStatus.AVAILABLE && currentLocation != null;
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public boolean isActive() {
        return active;
    }

    public DeliveryPartnerStatus getStatus() {
        return status;
    }

    public GeoLocation getCurrentLocation() {
        return currentLocation;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
