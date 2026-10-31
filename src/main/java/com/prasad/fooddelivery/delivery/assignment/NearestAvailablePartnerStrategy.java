package com.prasad.fooddelivery.delivery.assignment;

import com.prasad.fooddelivery.common.geo.GeoLocation;
import com.prasad.fooddelivery.delivery.model.Delivery;
import com.prasad.fooddelivery.delivery.model.DeliveryPartner;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// Picks the available partner closest to the restaurant, within a maximum radius.
public class NearestAvailablePartnerStrategy implements DeliveryAssignmentStrategy {

    private final double maxDistanceKm;

    public NearestAvailablePartnerStrategy(double maxDistanceKm) {
        if (maxDistanceKm <= 0) {
            throw new IllegalArgumentException("Maximum distance must be greater than zero");
        }
        this.maxDistanceKm = maxDistanceKm;
    }

    @Override
    public Optional<DeliveryPartner> selectPartner(Delivery delivery, List<DeliveryPartner> candidates) {
        if (delivery == null) {
            throw new IllegalArgumentException("Delivery is required");
        }

        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }

        GeoLocation pickup = delivery.getPickupLocation();

        return candidates.stream()
                .filter(DeliveryPartner::isAvailableForAssignment)
                .filter(partner -> partner.getCurrentLocation().distanceKmTo(pickup) <= maxDistanceKm)
                .min(Comparator.comparingDouble(partner -> partner.getCurrentLocation().distanceKmTo(pickup)));
    }
}
