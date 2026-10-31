package com.prasad.fooddelivery.common.geo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoLocationTest {

    @Test
    void shouldBeZeroDistanceToItself() {
        GeoLocation mgRoad = new GeoLocation(12.9756, 77.6066);

        assertEquals(0.0, mgRoad.distanceKmTo(mgRoad), 0.0001);
    }

    @Test
    void shouldCalculateDistanceBetweenCities() {
        GeoLocation bengaluru = new GeoLocation(12.9716, 77.5946);
        GeoLocation chennai = new GeoLocation(13.0827, 80.2707);

        // Straight-line distance is about 290 km
        double distance = bengaluru.distanceKmTo(chennai);
        assertTrue(distance > 280 && distance < 300, "was " + distance);
    }

    @Test
    void shouldBeEqualWhenCoordinatesAreEqual() {
        assertEquals(new GeoLocation(12.9756, 77.6066), new GeoLocation(12.9756, 77.6066));
    }

    @Test
    void shouldRejectInvalidLatitude() {
        assertThrows(IllegalArgumentException.class, () -> new GeoLocation(91, 77.6));
    }

    @Test
    void shouldRejectInvalidLongitude() {
        assertThrows(IllegalArgumentException.class, () -> new GeoLocation(12.9, 181));
    }
}
