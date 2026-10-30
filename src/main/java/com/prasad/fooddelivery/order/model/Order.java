package com.prasad.fooddelivery.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final UUID customerId;
    private final UUID restaurantId;
    private final UUID deliveryAddressId;

    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;

    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    private String specialInstructions;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order(
            UUID customerId,
            UUID restaurantId,
            UUID deliveryAddressId,
            BigDecimal subtotal,
            BigDecimal deliveryFee,
            BigDecimal taxAmount,
            BigDecimal discountAmount,
            String specialInstructions
    ) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID is required");
        }

        if (deliveryAddressId == null) {
            throw new IllegalArgumentException("Delivery address ID is required");
        }

        validateAmount(subtotal, "Subtotal");
        validateAmount(deliveryFee, "Delivery fee");
        validateAmount(taxAmount, "Tax amount");
        validateAmount(discountAmount, "Discount amount");

        if (discountAmount.compareTo(subtotal.add(deliveryFee).add(taxAmount)) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed the order amount");
        }

        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.deliveryAddressId = deliveryAddressId;

        this.orderStatus = OrderStatus.PLACED;
        this.paymentStatus = PaymentStatus.PENDING;

        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;

        this.totalAmount = subtotal
                .add(deliveryFee)
                .add(taxAmount)
                .subtract(discountAmount);

        this.specialInstructions = specialInstructions;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private void validateAmount(BigDecimal amount, String fieldName) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be null or negative");
        }
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        if (orderStatus == null) {
            throw new IllegalArgumentException("Order status is required");
        }

        this.orderStatus = orderStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            throw new IllegalArgumentException("Payment status is required");
        }

        this.paymentStatus = paymentStatus;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public UUID getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}