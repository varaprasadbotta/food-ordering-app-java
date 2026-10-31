package com.prasad.fooddelivery.restaurant.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Restaurant {

    private final UUID id;
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
    private final Instant createdAt;
    private Instant updatedAt;


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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }


    private static final BigDecimal MAX_COMMISSION_PERCENTAGE = new BigDecimal("100");

    public Restaurant(
            String name,
            String description,
            String phoneNumber,
            String email,
            List<String> cuisineTypes,
            boolean pureVeg,
            BigDecimal commissionPercentage
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Restaurant name is required");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        if (commissionPercentage == null
                || commissionPercentage.compareTo(BigDecimal.ZERO) < 0
                || commissionPercentage.compareTo(MAX_COMMISSION_PERCENTAGE) > 0) {
            throw new IllegalArgumentException("Commission percentage must be between 0 and 100");
        }

        this.id = UUID.randomUUID();

        this.name = name;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.email = email;

        this.cuisineTypes = cuisineTypes == null
                ? new ArrayList<>()
                : new ArrayList<>(cuisineTypes);

        this.averageRating = BigDecimal.ZERO;
        this.totalRatingsCount = 0;

        this.pureVeg = pureVeg;
        this.active = true;
        this.acceptingOrders = true;

        this.commissionPercentage = commissionPercentage;

        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Used only by restore() to rebuild an existing restaurant
    private Restaurant(
            UUID id,
            String name,
            String description,
            String phoneNumber,
            String email,
            List<String> cuisineTypes,
            BigDecimal averageRating,
            int totalRatingsCount,
            boolean pureVeg,
            boolean active,
            boolean acceptingOrders,
            BigDecimal commissionPercentage,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.cuisineTypes = cuisineTypes == null
                ? new ArrayList<>()
                : new ArrayList<>(cuisineTypes);
        this.averageRating = averageRating;
        this.totalRatingsCount = totalRatingsCount;
        this.pureVeg = pureVeg;
        this.active = active;
        this.acceptingOrders = acceptingOrders;
        this.commissionPercentage = commissionPercentage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Rebuilds a restaurant that already exists (e.g. loaded from the database)
    public static Restaurant restore(
            UUID id,
            String name,
            String description,
            String phoneNumber,
            String email,
            List<String> cuisineTypes,
            BigDecimal averageRating,
            int totalRatingsCount,
            boolean pureVeg,
            boolean active,
            boolean acceptingOrders,
            BigDecimal commissionPercentage,
            Instant createdAt,
            Instant updatedAt
    ) {
        if (id == null) {
            throw new IllegalArgumentException("Restaurant ID is required");
        }

        return new Restaurant(id, name, description, phoneNumber, email, cuisineTypes,
                averageRating, totalRatingsCount, pureVeg, active, acceptingOrders,
                commissionPercentage, createdAt, updatedAt);
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.acceptingOrders = false;
        this.updatedAt = Instant.now();
    }

    public void startAcceptingOrders() {
        if (!active) {
            throw new IllegalStateException(
                    "Inactive restaurant cannot accept orders"
            );
        }

        this.acceptingOrders = true;
        this.updatedAt = Instant.now();
    }

    public void stopAcceptingOrders() {
        this.acceptingOrders = false;
        this.updatedAt = Instant.now();
    }

}