package com.prasad.fooddelivery.order.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order createOrder() {
        return new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("540.00"),
                new BigDecimal("40.00"),
                new BigDecimal("27.00"),
                new BigDecimal("50.00"),
                "Deliver without ringing the bell"
        );
    }

    @Test
    void shouldCreateOrder() {
        Order order = createOrder();

        assertNotNull(order.getId());
        assertNotNull(order.getCustomerId());
        assertNotNull(order.getRestaurantId());
        assertNotNull(order.getDeliveryAddressId());

        assertEquals(OrderStatus.PLACED, order.getOrderStatus());
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());

        assertEquals(new BigDecimal("540.00"), order.getSubtotal());
        assertEquals(new BigDecimal("40.00"), order.getDeliveryFee());
        assertEquals(new BigDecimal("27.00"), order.getTaxAmount());
        assertEquals(new BigDecimal("50.00"), order.getDiscountAmount());
        assertEquals(new BigDecimal("557.00"), order.getTotalAmount());

        assertFalse(order.getSpecialInstructions().isBlank());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void shouldRejectNullCustomerId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Order(
                        null,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new BigDecimal("540"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        null
                )
        );
    }

    @Test
    void shouldRejectNullRestaurantId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Order(
                        UUID.randomUUID(),
                        null,
                        UUID.randomUUID(),
                        new BigDecimal("540"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        null
                )
        );
    }

    @Test
    void shouldRejectNullDeliveryAddressId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Order(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        new BigDecimal("540"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        null
                )
        );
    }

    @Test
    void shouldRejectNegativeSubtotal() {
        assertThrows(IllegalArgumentException.class, () ->
                new Order(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new BigDecimal("-100"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        null
                )
        );
    }

    @Test
    void shouldRejectNegativeDeliveryFee() {
        assertThrows(IllegalArgumentException.class, () ->
                new Order(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new BigDecimal("100"),
                        new BigDecimal("-20"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        null
                )
        );
    }

    @Test
    void shouldRejectNegativeTaxAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                new Order(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new BigDecimal("100"),
                        BigDecimal.ZERO,
                        new BigDecimal("-10"),
                        BigDecimal.ZERO,
                        null
                )
        );
    }

    @Test
    void shouldRejectDiscountGreaterThanOrderAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                new Order(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new BigDecimal("100"),
                        new BigDecimal("20"),
                        new BigDecimal("10"),
                        new BigDecimal("150"),
                        null
                )
        );
    }

    @Test
    void shouldUpdateOrderStatus() {
        Order order = createOrder();

        order.updateOrderStatus(OrderStatus.CONFIRMED);

        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());
    }

    @Test
    void shouldRejectNullOrderStatus() {
        Order order = createOrder();

        assertThrows(IllegalArgumentException.class, () ->
                order.updateOrderStatus(null)
        );
    }

    @Test
    void shouldUpdatePaymentStatus() {
        Order order = createOrder();

        order.updatePaymentStatus(PaymentStatus.SUCCESS);

        assertEquals(PaymentStatus.SUCCESS, order.getPaymentStatus());
    }

    @Test
    void shouldRejectNullPaymentStatus() {
        Order order = createOrder();

        assertThrows(IllegalArgumentException.class, () ->
                order.updatePaymentStatus(null)
        );
    }

    @Test
    void shouldCalculateTotalAmountWithoutDiscount() {
        Order order = new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("500"),
                new BigDecimal("40"),
                new BigDecimal("27"),
                BigDecimal.ZERO,
                null
        );

        assertEquals(new BigDecimal("567"), order.getTotalAmount());
    }

    @Test
    void shouldFollowFullLifecycleUntilDelivered() {
        Order order = createOrder();

        order.updateOrderStatus(OrderStatus.CONFIRMED);
        order.updateOrderStatus(OrderStatus.PREPARING);
        order.updateOrderStatus(OrderStatus.READY_FOR_PICKUP);
        order.updateOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
        order.updateOrderStatus(OrderStatus.DELIVERED);

        assertEquals(OrderStatus.DELIVERED, order.getOrderStatus());
    }

    @Test
    void shouldRejectSkippingStatuses() {
        Order order = createOrder();

        assertThrows(IllegalStateException.class, () ->
                order.updateOrderStatus(OrderStatus.DELIVERED)
        );
        assertEquals(OrderStatus.PLACED, order.getOrderStatus());
    }

    @Test
    void shouldAllowCancellationBeforePreparation() {
        Order order = createOrder();
        order.updateOrderStatus(OrderStatus.CONFIRMED);

        order.updateOrderStatus(OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
    }

    @Test
    void shouldRejectCancellationOncePreparing() {
        Order order = createOrder();
        order.updateOrderStatus(OrderStatus.CONFIRMED);
        order.updateOrderStatus(OrderStatus.PREPARING);

        assertThrows(IllegalStateException.class, () ->
                order.updateOrderStatus(OrderStatus.CANCELLED)
        );
    }

    @Test
    void shouldRejectAnyChangeAfterCancellation() {
        Order order = createOrder();
        order.updateOrderStatus(OrderStatus.CANCELLED);

        assertThrows(IllegalStateException.class, () ->
                order.updateOrderStatus(OrderStatus.CONFIRMED)
        );
    }

    @Test
    void shouldAllowPaymentSuccessAfterFailedAttempt() {
        Order order = createOrder();
        order.updatePaymentStatus(PaymentStatus.FAILED);

        order.updatePaymentStatus(PaymentStatus.SUCCESS);

        assertEquals(PaymentStatus.SUCCESS, order.getPaymentStatus());
    }

    @Test
    void shouldRejectRefundOfUnpaidOrder() {
        Order order = createOrder();

        assertThrows(IllegalStateException.class, () ->
                order.updatePaymentStatus(PaymentStatus.REFUNDED)
        );
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
    }

    @Test
    void shouldAllowRefundOfPaidOrder() {
        Order order = createOrder();
        order.updatePaymentStatus(PaymentStatus.SUCCESS);

        order.updatePaymentStatus(PaymentStatus.REFUNDED);

        assertEquals(PaymentStatus.REFUNDED, order.getPaymentStatus());
    }

    @Test
    void shouldRestoreExistingOrderWithoutResettingStatusOrTotals() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-10T10:15:30Z");

        Order order = Order.restore(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                OrderStatus.OUT_FOR_DELIVERY,
                PaymentStatus.SUCCESS,
                new BigDecimal("540.00"),
                new BigDecimal("40.00"),
                new BigDecimal("27.00"),
                new BigDecimal("50.00"),
                new BigDecimal("557.00"),
                null,
                createdAt,
                createdAt
        );

        assertEquals(id, order.getId());
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getOrderStatus());
        assertEquals(PaymentStatus.SUCCESS, order.getPaymentStatus());
        assertEquals(new BigDecimal("557.00"), order.getTotalAmount());
        assertEquals(createdAt, order.getCreatedAt());

        // Transition rules still apply to a restored order
        order.updateOrderStatus(OrderStatus.DELIVERED);
        assertEquals(OrderStatus.DELIVERED, order.getOrderStatus());
    }
}