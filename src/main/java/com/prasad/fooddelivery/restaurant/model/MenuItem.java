package com.prasad.fooddelivery.restaurant.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class MenuItem {

    private final UUID id;
    private final UUID restaurantId;

    private String name;
    private String description;

    private BigDecimal price;

    private MenuCategory category;
    private DietaryType dietaryType;

    private String imageUrl;

    private boolean available;
    private boolean recommended;

    private final Instant createdAt;
    private Instant updatedAt;

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

        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Used only by restore() to rebuild an existing menu item
    private MenuItem(
            UUID id,
            UUID restaurantId,
            String name,
            String description,
            BigDecimal price,
            MenuCategory category,
            DietaryType dietaryType,
            String imageUrl,
            boolean available,
            boolean recommended,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.dietaryType = dietaryType;
        this.imageUrl = imageUrl;
        this.available = available;
        this.recommended = recommended;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Rebuilds a menu item that already exists (e.g. loaded from the database)
    public static MenuItem restore(
            UUID id,
            UUID restaurantId,
            String name,
            String description,
            BigDecimal price,
            MenuCategory category,
            DietaryType dietaryType,
            String imageUrl,
            boolean available,
            boolean recommended,
            Instant createdAt,
            Instant updatedAt
    ) {
        if (id == null) {
            throw new IllegalArgumentException("Menu item ID is required");
        }

        return new MenuItem(id, restaurantId, name, description, price, category,
                dietaryType, imageUrl, available, recommended, createdAt, updatedAt);
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updatePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        this.price = newPrice;
        this.updatedAt = Instant.now();
    }

    public void markUnavailable() {
        this.available = false;
        this.updatedAt = Instant.now();
    }

    public void markAvailable() {
        this.available = true;
        this.updatedAt = Instant.now();
    }

    public void markAsRecommended() {
        this.recommended = true;
        this.updatedAt = Instant.now();
    }

    public void removeRecommendation() {
        this.recommended = false;
        this.updatedAt = Instant.now();
    }

}