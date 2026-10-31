package com.prasad.fooddelivery.order.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    private OrderItem createOrderItem() {
        return new OrderItem(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Chicken Biryani",
                new BigDecimal("250.00"),
                2,
                "Less spicy"
        );
    }

    @Test
    void shouldCreateOrderItem() {
        OrderItem item = createOrderItem();

        assertNotNull(item.getId());
        assertNotNull(item.getOrderId());
        assertNotNull(item.getMenuItemId());

        assertEquals("Chicken Biryani", item.getItemName());
        assertEquals(new BigDecimal("250.00"), item.getUnitPrice());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("500.00"), item.getTotalPrice());
        assertEquals("Less spicy", item.getSpecialInstructions());

        assertNotNull(item.getCreatedAt());
        assertNotNull(item.getUpdatedAt());
    }

    @Test
    void shouldRejectNullOrderId() {
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(
                        null,
                        UUID.randomUUID(),
                        "Chicken Biryani",
                        new BigDecimal("250"),
                        2,
                        null
                )
        );
    }

    @Test
    void shouldRejectNullMenuItemId() {
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(
                        UUID.randomUUID(),
                        null,
                        "Chicken Biryani",
                        new BigDecimal("250"),
                        2,
                        null
                )
        );
    }

    @Test
    void shouldRejectBlankItemName() {
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        " ",
                        new BigDecimal("250"),
                        2,
                        null
                )
        );
    }

    @Test
    void shouldRejectZeroUnitPrice() {
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "Chicken Biryani",
                        BigDecimal.ZERO,
                        2,
                        null
                )
        );
    }

    @Test
    void shouldRejectNegativeUnitPrice() {
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "Chicken Biryani",
                        new BigDecimal("-100"),
                        2,
                        null
                )
        );
    }

    @Test
    void shouldRejectZeroQuantity() {
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "Chicken Biryani",
                        new BigDecimal("250"),
                        0,
                        null
                )
        );
    }

    @Test
    void shouldRejectNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "Chicken Biryani",
                        new BigDecimal("250"),
                        -1,
                        null
                )
        );
    }

    @Test
    void shouldUpdateQuantityAndRecalculateTotal() {
        OrderItem item = createOrderItem();

        item.updateQuantity(3);

        assertEquals(3, item.getQuantity());
        assertEquals(new BigDecimal("750.00"), item.getTotalPrice());
    }

    @Test
    void shouldRejectInvalidQuantityDuringUpdate() {
        OrderItem item = createOrderItem();

        assertThrows(IllegalArgumentException.class, () ->
                item.updateQuantity(0)
        );

        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("500.00"), item.getTotalPrice());
    }

    @Test
    void shouldUpdateSpecialInstructions() {
        OrderItem item = createOrderItem();

        item.updateSpecialInstructions("Extra spicy");

        assertEquals("Extra spicy", item.getSpecialInstructions());
    }

    @Test
    void shouldPreserveOriginalPrice() {
        OrderItem item = createOrderItem();

        item.updateQuantity(4);

        assertEquals(new BigDecimal("250.00"), item.getUnitPrice());
        assertEquals(new BigDecimal("1000.00"), item.getTotalPrice());
    }
}