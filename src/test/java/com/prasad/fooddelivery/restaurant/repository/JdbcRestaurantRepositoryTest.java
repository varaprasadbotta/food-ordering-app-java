package com.prasad.fooddelivery.restaurant.repository;

import com.prasad.fooddelivery.common.db.PostgresTestDatabase;
import com.prasad.fooddelivery.common.exception.DataAccessException;
import com.prasad.fooddelivery.restaurant.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JdbcRestaurantRepositoryTest {

    private RestaurantRepository repository;

    @BeforeEach
    void setUp() {
        PostgresTestDatabase.clearAllTables();
        repository = new JdbcRestaurantRepository(PostgresTestDatabase.dataSource());
    }

    private Restaurant createRestaurant(List<String> cuisines) {
        return new Restaurant(
                "Paradise",
                "Famous Biryani Restaurant",
                "9876543210",
                "paradise@example.com",
                cuisines,
                false,
                new BigDecimal("10.00")
        );
    }

    // Same id as the given restaurant, but with a different cuisine list
    private Restaurant withCuisines(Restaurant restaurant, List<String> cuisines) {
        return Restaurant.restore(
                restaurant.getId(), restaurant.getName(), restaurant.getDescription(),
                restaurant.getPhoneNumber(), restaurant.getEmail(), cuisines,
                restaurant.getAverageRating(), restaurant.getTotalRatingsCount(),
                restaurant.isPureVeg(), restaurant.isActive(), restaurant.isAcceptingOrders(),
                restaurant.getCommissionPercentage(), restaurant.getCreatedAt(), Instant.now()
        );
    }

    @Test
    void shouldSaveAndFindRestaurantById() {
        Restaurant restaurant = createRestaurant(List.of("Biryani", "North Indian"));

        repository.save(restaurant);
        Restaurant found = repository.findById(restaurant.getId()).orElseThrow();

        assertEquals(restaurant.getId(), found.getId());
        assertEquals("Paradise", found.getName());
        assertEquals("Famous Biryani Restaurant", found.getDescription());
        assertEquals("9876543210", found.getPhoneNumber());
        assertEquals("paradise@example.com", found.getEmail());
        assertEquals(0, BigDecimal.ZERO.compareTo(found.getAverageRating()));
        assertEquals(0, found.getTotalRatingsCount());
        assertFalse(found.isPureVeg());
        assertTrue(found.isActive());
        assertTrue(found.isAcceptingOrders());
        assertEquals(0, new BigDecimal("10.00").compareTo(found.getCommissionPercentage()));
    }

    @Test
    void shouldKeepCuisinesInOriginalOrder() {
        Restaurant restaurant = createRestaurant(List.of("North Indian", "Biryani", "Chinese"));

        repository.save(restaurant);

        assertEquals(
                List.of("North Indian", "Biryani", "Chinese"),
                repository.findById(restaurant.getId()).orElseThrow().getCuisineTypes()
        );
    }

    @Test
    void shouldSaveRestaurantWithoutCuisines() {
        Restaurant restaurant = createRestaurant(null);

        repository.save(restaurant);

        assertTrue(repository.findById(restaurant.getId()).orElseThrow().getCuisineTypes().isEmpty());
    }

    @Test
    void shouldReplaceCuisinesWhenSavedAgain() {
        Restaurant restaurant = createRestaurant(List.of("Biryani", "North Indian"));
        repository.save(restaurant);

        repository.save(withCuisines(restaurant, List.of("Chinese")));

        assertEquals(
                List.of("Chinese"),
                repository.findById(restaurant.getId()).orElseThrow().getCuisineTypes()
        );
    }

    @Test
    void shouldUpdateStateWhenSavedAgain() {
        Restaurant restaurant = createRestaurant(List.of("Biryani"));
        repository.save(restaurant);

        restaurant.deactivate();
        repository.save(restaurant);

        Restaurant found = repository.findById(restaurant.getId()).orElseThrow();
        assertFalse(found.isActive());
        assertFalse(found.isAcceptingOrders());
    }

    @Test
    void shouldRollBackRestaurantWhenSavingCuisinesFails() {
        // The same cuisine twice breaks the (restaurant_id, cuisine) primary key,
        // which happens after the restaurant row was already inserted.
        Restaurant restaurant = createRestaurant(List.of("Biryani", "Biryani"));

        assertThrows(DataAccessException.class, () -> repository.save(restaurant));

        assertTrue(repository.findById(restaurant.getId()).isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenRestaurantNotFound() {
        assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
    }

    @Test
    void shouldRejectNullRestaurant() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void shouldRejectNullId() {
        assertThrows(IllegalArgumentException.class, () -> repository.findById(null));
    }
}
