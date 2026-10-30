package com.prasad.fooddelivery.restaurant.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    private final UUID restaurantId = UUID.randomUUID();

    private MenuItem createMenuItem() {
        return new MenuItem(
                restaurantId,
                "Chicken Biryani",
                "Hyderabadi Chicken Biryani",
                new BigDecimal("250"),
                MenuCategory.MAIN_COURSE,
                DietaryType.NON_VEG,
                null
        );
    }

    @Test
    void shouldCreateMenuItem() {

        MenuItem item = createMenuItem();

        assertNotNull(item.getId());
        assertEquals(restaurantId, item.getRestaurantId());
        assertEquals("Chicken Biryani", item.getName());
        assertEquals(new BigDecimal("250"), item.getPrice());
        assertEquals(MenuCategory.MAIN_COURSE, item.getCategory());
        assertEquals(DietaryType.NON_VEG, item.getDietaryType());
        assertTrue(item.isAvailable());
        assertFalse(item.isRecommended());
    }

    @Test
    void shouldRejectNegativePriceDuringCreation() {

        assertThrows(IllegalArgumentException.class, () ->
                new MenuItem(
                        restaurantId,
                        "Chicken Biryani",
                        "Biryani",
                        new BigDecimal("-100"),
                        MenuCategory.MAIN_COURSE,
                        DietaryType.NON_VEG,
                        null
                )
        );
    }

    @Test
    void shouldRejectZeroPriceDuringCreation() {

        assertThrows(IllegalArgumentException.class, () ->
                new MenuItem(
                        restaurantId,
                        "Chicken Biryani",
                        "Biryani",
                        BigDecimal.ZERO,
                        MenuCategory.MAIN_COURSE,
                        DietaryType.NON_VEG,
                        null
                )
        );
    }

    @Test
    void shouldRejectInvalidPriceDuringUpdate() {

        MenuItem item = createMenuItem();

        assertThrows(
                IllegalArgumentException.class,
                () -> item.updatePrice(new BigDecimal("-100"))
        );

        assertEquals(new BigDecimal("250"), item.getPrice());
    }

    @Test
    void shouldUpdatePrice() {

        MenuItem item = createMenuItem();

        item.updatePrice(new BigDecimal("300"));

        assertEquals(new BigDecimal("300"), item.getPrice());
    }

    @Test
    void shouldMarkMenuItemUnavailable() {

        MenuItem item = createMenuItem();

        item.markUnavailable();

        assertFalse(item.isAvailable());
    }

    @Test
    void shouldMarkMenuItemAsRecommended() {

        MenuItem item = createMenuItem();

        item.markAsRecommended();

        assertTrue(item.isRecommended());
    }
}