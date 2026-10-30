package com.prasad.fooddelivery.restaurant.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Restaurant {

    private UUID id;
    private String name;
    private String description;
    private String phoneNumber;
    private String email;
    private List<String> cuisineTypes;
    private BigDecimal averageRating;
    private int totalRatingsCount;
    private boolean pureVeg;
    private boolean active;
    private boolean acceptingOrders;
    private BigDecimal commissionPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getCuisineTypes() {
        return List.copyOf(cuisineTypes);
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public int getTotalRatingsCount() {
        return totalRatingsCount;
    }

    public boolean isPureVeg() {
        return pureVeg;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAcceptingOrders() {
        return acceptingOrders;
    }

    public BigDecimal getCommissionPercentage() {
        return commissionPercentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    public Restaurant(
            String name,
            String description,
            String phoneNumber,
            String email,
            List<String> cuisineTypes,
            BigDecimal commissionPercentage
    ) {
        this.id = UUID.randomUUID();

        this.name = name;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.email = email;

        this.cuisineTypes = cuisineTypes == null
                ? new java.util.ArrayList<>()
                : new java.util.ArrayList<>(cuisineTypes);

        this.averageRating = BigDecimal.ZERO;
        this.totalRatingsCount = 0;

        this.pureVeg = false;
        this.active = true;
        this.acceptingOrders = true;

        this.commissionPercentage = commissionPercentage;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.acceptingOrders = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void startAcceptingOrders() {
        if (!active) {
            throw new IllegalStateException(
                    "Inactive restaurant cannot accept orders"
            );
        }

        this.acceptingOrders = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void stopAcceptingOrders() {
        this.acceptingOrders = false;
        this.updatedAt = LocalDateTime.now();
    }

}