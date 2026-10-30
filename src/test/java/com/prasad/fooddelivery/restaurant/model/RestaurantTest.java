package com.prasad.fooddelivery.restaurant.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
                new BigDecimal("10.00")
        );

        restaurant.stopAcceptingOrders();

        assertFalse(restaurant.isAcceptingOrders());
        assertTrue(restaurant.isActive());
    }
}