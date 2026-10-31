package com.prasad.fooddelivery.order.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class OrderItem {

    private final UUID id;
    private final UUID orderId;
    private final UUID menuItemId;

    private final String itemName;
    private final BigDecimal unitPrice;

    private int quantity;
    private BigDecimal totalPrice;

    private String specialInstructions;

    private final Instant createdAt;
    private Instant updatedAt;

    public OrderItem(
            UUID orderId,
            UUID menuItemId,
            String itemName,
            BigDecimal unitPrice,
            int quantity,
            String specialInstructions
    ) {

        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is required");
        }

        if (menuItemId == null) {
            throw new IllegalArgumentException("Menu item ID is required");
        }

        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("Item name is required");
        }

        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be greater than zero");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.menuItemId = menuItemId;

        this.itemName = itemName;
        this.unitPrice = unitPrice;

        this.quantity = quantity;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

        this.specialInstructions = specialInstructions;

        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Used only by restore() to rebuild an existing order item
    private OrderItem(
            UUID id,
            UUID orderId,
            UUID menuItemId,
            String itemName,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal totalPrice,
            String specialInstructions,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.specialInstructions = specialInstructions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Rebuilds an order item that already exists (e.g. loaded from the database)
    public static OrderItem restore(
            UUID id,
            UUID orderId,
            UUID menuItemId,
            String itemName,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal totalPrice,
            String specialInstructions,
            Instant createdAt,
            Instant updatedAt
    ) {
        if (id == null) {
            throw new IllegalArgumentException("Order item ID is required");
        }

        return new OrderItem(id, orderId, menuItemId, itemName, unitPrice,
                quantity, totalPrice, specialInstructions, createdAt, updatedAt);
    }

    public void updateQuantity(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        this.quantity = quantity;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

        this.updatedAt = Instant.now();
    }

    public void updateSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
        this.updatedAt = Instant.now();
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getMenuItemId() {
        return menuItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}