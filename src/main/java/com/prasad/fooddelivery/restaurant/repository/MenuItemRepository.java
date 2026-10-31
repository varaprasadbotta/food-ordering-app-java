package com.prasad.fooddelivery.restaurant.repository;

import com.prasad.fooddelivery.restaurant.model.MenuItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuItemRepository {

    // Inserts a new menu item, or updates the existing one with the same id
    MenuItem save(MenuItem menuItem);

    Optional<MenuItem> findById(UUID menuItemId);

    // A restaurant's full menu, including unavailable items (the restaurant's own view needs them)
    List<MenuItem> findByRestaurantId(UUID restaurantId);
}
