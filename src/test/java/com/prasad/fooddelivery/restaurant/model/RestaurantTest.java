package com.prasad.fooddelivery.restaurant.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Test
    void shouldCreateRestaurant() {

        Restaurant restaurant = new Restaurant(
                "Paradise",
                "Famous Biryani Restaurant",
                "9876543210",
                "paradise@example.com",
                List.of("Biryani", "North Indian"),
                false,
                new BigDecimal("10.00")
        );

        assertNotNull(restaurant.getId());
        assertEquals("Paradise", restaurant.getName());
        assertEquals(2, restaurant.getCuisineTypes().size());
        assertEquals(BigDecimal.ZERO, restaurant.getAverageRating());
        assertTrue(restaurant.isActive());
        assertTrue(restaurant.isAcceptingOrders());
        assertFalse(restaurant.isPureVeg());
    }

    @Test
    void shouldAllowEmptyCuisineTypes() {

        Restaurant restaurant = new Restaurant(
                "New Restaurant",
                "New restaurant description",
                "9876543210",
                "new@example.com",
                null,
                false,
                new BigDecimal("8.00")
        );

        assertTrue(restaurant.getCuisineTypes().isEmpty());
    }

    @Test
    void shouldNotAllowInactiveRestaurantToAcceptOrders() {

        Restaurant restaurant = new Restaurant(
                "Paradise",
                "Biryani Restaurant",
                "9876543210",
                "paradise@example.com",
                List.of("Biryani"),
                false,
                new BigDecimal("10.00")
        );

        restaurant.deactivate();

        assertThrows(
                IllegalStateException.class,
                restaurant::startAcceptingOrders
        );

        assertFalse(restaurant.isAcceptingOrders());
    }

    @Test
    void shouldStopAcceptingOrders() {

        Restaurant restaurant = new Restaurant(
                "Paradise",
                "Biryani Restaurant",
                "9876543210",
                "paradise@example.com",
                List.of("Biryani"),
                false,
                new BigDecimal("10.00")
        );

        restaurant.stopAcceptingOrders();

        assertFalse(restaurant.isAcceptingOrders());
        assertTrue(restaurant.isActive());
    }

    @Test
    void shouldCreatePureVegRestaurant() {

        Restaurant restaurant = new Restaurant(
                "Saravana Bhavan",
                "South Indian vegetarian",
                "9876543210",
                "saravana@example.com",
                List.of("South Indian"),
                true,
                new BigDecimal("12.00")
        );

        assertTrue(restaurant.isPureVeg());
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Restaurant(
                        " ",
                        "Description",
                        "9876543210",
                        "test@example.com",
                        List.of("Biryani"),
                        false,
                        new BigDecimal("10.00")
                )
        );
    }

    @Test
    void shouldRejectCommissionAboveHundredPercent() {
        assertThrows(IllegalArgumentException.class, () ->
                new Restaurant(
                        "Paradise",
                        "Description",
                        "9876543210",
                        "paradise@example.com",
                        List.of("Biryani"),
                        false,
                        new BigDecimal("250.00")
                )
        );
    }

    @Test
    void shouldRejectNegativeCommission() {
        assertThrows(IllegalArgumentException.class, () ->
                new Restaurant(
                        "Paradise",
                        "Description",
                        "9876543210",
                        "paradise@example.com",
                        List.of("Biryani"),
                        false,
                        new BigDecimal("-1.00")
                )
        );
    }

    @Test
    void shouldRestoreExistingRestaurantWithStoredState() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-10T10:15:30Z");
        Instant updatedAt = Instant.parse("2026-02-01T08:00:00Z");

        Restaurant restaurant = Restaurant.restore(
                id,
                "Paradise",
                "Biryani Restaurant",
                "9876543210",
                "paradise@example.com",
                List.of("Biryani"),
                new BigDecimal("4.3"),
                1200,
                false,
                true,
                false,
                new BigDecimal("10.00"),
                createdAt,
                updatedAt
        );

        assertEquals(id, restaurant.getId());
        assertEquals(new BigDecimal("4.3"), restaurant.getAverageRating());
        assertEquals(1200, restaurant.getTotalRatingsCount());
        assertFalse(restaurant.isAcceptingOrders());
        assertEquals(createdAt, restaurant.getCreatedAt());
        assertEquals(updatedAt, restaurant.getUpdatedAt());
    }
}