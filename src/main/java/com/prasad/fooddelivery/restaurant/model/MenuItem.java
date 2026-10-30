package com.prasad.fooddelivery.restaurant.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class MenuItem {

    private UUID id;
    private UUID restaurantId;

    private String name;
    private String description;

    private BigDecimal price;

    private MenuCategory category;
    private DietaryType dietaryType;

    private String imageUrl;

    private boolean available;
    private boolean recommended;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MenuItem(
            UUID restaurantId,
            String name,
            String description,
            BigDecimal price,
            MenuCategory category,
            DietaryType dietaryType,
            String imageUrl
    ) {
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID is required");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Menu item name is required");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        if (category == null) {
            throw new IllegalArgumentException("Menu category is required");
        }

        if (dietaryType == null) {
            throw new IllegalArgumentException("Dietary type is required");
        }

        this.id = UUID.randomUUID();
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.dietaryType = dietaryType;
        this.imageUrl = imageUrl;

        this.available = true;
        this.recommended = false;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public MenuCategory getCategory() {
        return category;
    }

    public DietaryType getDietaryType() {
        return dietaryType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updatePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        this.price = newPrice;
        this.updatedAt = LocalDateTime.now();
    }

    public void markUnavailable() {
        this.available = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAvailable() {
        this.available = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsRecommended() {
        this.recommended = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void removeRecommendation() {
        this.recommended = false;
        this.updatedAt = LocalDateTime.now();
    }

}