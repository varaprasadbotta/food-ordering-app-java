package com.prasad.fooddelivery.common.geo;

// Immutable value object: two locations with the same coordinates are equal,
// and a location never changes - a partner who moves gets a new GeoLocation.
// double is fine here (unlike money): GPS coordinates are approximate anyway.
public record GeoLocation(double latitude, double longitude) {

    private static final double EARTH_RADIUS_KM = 6371.0;

    public GeoLocation {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
    }

    // Straight-line ("as the crow flies") distance using the haversine formula.
    // Real systems use road distance from a maps API; this is a good-enough first version.
    public double distanceKmTo(GeoLocation other) {
        double latDistance = Math.toRadians(other.latitude - latitude);
        double lonDistance = Math.toRadians(other.longitude - longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
