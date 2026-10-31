package com.prasad.fooddelivery.restaurant.repository;

import com.prasad.fooddelivery.common.db.PostgresTestDatabase;
import com.prasad.fooddelivery.common.exception.DataAccessException;
import com.prasad.fooddelivery.restaurant.model.DietaryType;
import com.prasad.fooddelivery.restaurant.model.MenuCategory;
import com.prasad.fooddelivery.restaurant.model.MenuItem;
import com.prasad.fooddelivery.restaurant.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JdbcMenuItemRepositoryTest {

    private MenuItemRepository repository;
    private RestaurantRepository restaurantRepository;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        PostgresTestDatabase.clearAllTables();
        repository = new JdbcMenuItemRepository(PostgresTestDatabase.dataSource());
        restaurantRepository = new JdbcRestaurantRepository(PostgresTestDatabase.dataSource());

        // menu_items.restaurant_id is a foreign key, so the restaurant must exist first
        restaurant = restaurantRepository.save(createRestaurant("Paradise"));
    }

    private Restaurant createRestaurant(String name) {
        return new Restaurant(name, "Description", "9876543210", "test@example.com",
                List.of("Biryani"), false, new BigDecimal("10.00"));
    }

    private MenuItem createMenuItem(UUID restaurantId, String name) {
        return new MenuItem(
                restaurantId,
                name,
                "Hyderabadi " + name,
                new BigDecimal("250.00"),
                MenuCategory.MAIN_COURSE,
                DietaryType.NON_VEG,
                "https://images.example.com/biryani.jpg"
        );
    }

    @Test
    void shouldSaveAndFindMenuItemById() {
        MenuItem item = createMenuItem(restaurant.getId(), "Chicken Biryani");

        repository.save(item);
        MenuItem found = repository.findById(item.getId()).orElseThrow();

        assertEquals(item.getId(), found.getId());
        assertEquals(restaurant.getId(), found.getRestaurantId());
        assertEquals("Chicken Biryani", found.getName());
        assertEquals("Hyderabadi Chicken Biryani", found.getDescription());
        assertEquals(new BigDecimal("250.00"), found.getPrice());
        assertEquals(MenuCategory.MAIN_COURSE, found.getCategory());
        assertEquals(DietaryType.NON_VEG, found.getDietaryType());
        assertEquals("https://images.example.com/biryani.jpg", found.getImageUrl());
        assertTrue(found.isAvailable());
        assertFalse(found.isRecommended());
    }

    @Test
    void shouldUpdateMenuItemWhenSavedAgain() {
        MenuItem item = createMenuItem(restaurant.getId(), "Chicken Biryani");
        repository.save(item);

        item.updatePrice(new BigDecimal("300.00"));
        item.markUnavailable();
        item.markAsRecommended();
        repository.save(item);

        MenuItem found = repository.findById(item.getId()).orElseThrow();
        assertEquals(new BigDecimal("300.00"), found.getPrice());
        assertFalse(found.isAvailable());
        assertTrue(found.isRecommended());
    }

    @Test
    void shouldFindOnlyItemsOfGivenRestaurant() {
        Restaurant otherRestaurant = restaurantRepository.save(createRestaurant("Bawarchi"));

        repository.save(createMenuItem(restaurant.getId(), "Chicken Biryani"));
        repository.save(createMenuItem(restaurant.getId(), "Mutton Biryani"));
        repository.save(createMenuItem(otherRestaurant.getId(), "Veg Biryani"));

        List<MenuItem> menu = repository.findByRestaurantId(restaurant.getId());

        assertEquals(2, menu.size());
        assertTrue(menu.stream().allMatch(item -> item.getRestaurantId().equals(restaurant.getId())));
    }

    @Test
    void shouldReturnEmptyMenuForRestaurantWithoutItems() {
        assertTrue(repository.findByRestaurantId(restaurant.getId()).isEmpty());
    }

    @Test
    void shouldRejectMenuItemForUnknownRestaurant() {
        MenuItem item = createMenuItem(UUID.randomUUID(), "Chicken Biryani");

        assertThrows(DataAccessException.class, () -> repository.save(item));
    }

    @Test
    void shouldReturnEmptyWhenMenuItemNotFound() {
        assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
    }

    @Test
    void shouldRejectNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
        assertThrows(IllegalArgumentException.class, () -> repository.findById(null));
        assertThrows(IllegalArgumentException.class, () -> repository.findByRestaurantId(null));
    }
}
