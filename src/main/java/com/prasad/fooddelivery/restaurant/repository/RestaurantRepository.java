package com.prasad.fooddelivery.restaurant.repository;

import com.prasad.fooddelivery.restaurant.model.Restaurant;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository {

    // Inserts a new restaurant, or updates the existing one with the same id
    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(UUID restaurantId);
}
