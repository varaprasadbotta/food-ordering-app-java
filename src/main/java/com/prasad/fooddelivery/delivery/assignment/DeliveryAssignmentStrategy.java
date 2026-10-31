package com.prasad.fooddelivery.delivery.assignment;

import com.prasad.fooddelivery.delivery.model.Delivery;
import com.prasad.fooddelivery.delivery.model.DeliveryPartner;

import java.util.List;
import java.util.Optional;

// Strategy pattern: "which partner should get this delivery?" has several valid answers
// (nearest, least busy today, highest rated, ...). DeliveryAssignmentService depends on this
// interface, so a new rule is a new class - the service itself does not change.
public interface DeliveryAssignmentStrategy {

    // Empty when no suitable partner is available right now
    Optional<DeliveryPartner> selectPartner(Delivery delivery, List<DeliveryPartner> candidates);
}
