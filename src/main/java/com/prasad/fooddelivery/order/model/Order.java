package com.prasad.fooddelivery.order.model;

import java.math.BigDecimal;
import java.time.Instant;
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

    private final Instant createdAt;
    private Instant updatedAt;

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

        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Used only by restore() to rebuild an existing order
    private Order(
            UUID id,
            UUID customerId,
            UUID restaurantId,
            UUID deliveryAddressId,
            OrderStatus orderStatus,
            PaymentStatus paymentStatus,
            BigDecimal subtotal,
            BigDecimal deliveryFee,
            BigDecimal taxAmount,
            BigDecimal discountAmount,
            BigDecimal totalAmount,
            String specialInstructions,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.deliveryAddressId = deliveryAddressId;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.specialInstructions = specialInstructions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Rebuilds an order that already exists (e.g. loaded from the database).
    // The stored status and totals are kept as they are, not reset or recalculated.
    public static Order restore(
            UUID id,
            UUID customerId,
            UUID restaurantId,
            UUID deliveryAddressId,
            OrderStatus orderStatus,
            PaymentStatus paymentStatus,
            BigDecimal subtotal,
            BigDecimal deliveryFee,
            BigDecimal taxAmount,
            BigDecimal discountAmount,
            BigDecimal totalAmount,
            String specialInstructions,
            Instant createdAt,
            Instant updatedAt
    ) {
        if (id == null) {
            throw new IllegalArgumentException("Order ID is required");
        }

        if (orderStatus == null || paymentStatus == null) {
            throw new IllegalArgumentException("Order and payment status are required");
        }

        return new Order(id, customerId, restaurantId, deliveryAddressId,
                orderStatus, paymentStatus, subtotal, deliveryFee, taxAmount,
                discountAmount, totalAmount, specialInstructions, createdAt, updatedAt);
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

        if (!this.orderStatus.canTransitionTo(orderStatus)) {
            throw new IllegalStateException(
                    "Cannot change order status from " + this.orderStatus + " to " + orderStatus
            );
        }

        this.orderStatus = orderStatus;
        this.updatedAt = Instant.now();
    }

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            throw new IllegalArgumentException("Payment status is required");
        }

        if (!this.paymentStatus.canTransitionTo(paymentStatus)) {
            throw new IllegalStateException(
                    "Cannot change payment status from " + this.paymentStatus + " to " + paymentStatus
            );
        }

        this.paymentStatus = paymentStatus;
        this.updatedAt = Instant.now();
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}