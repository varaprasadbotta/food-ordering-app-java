package com.prasad.fooddelivery.customer.repository;

import com.prasad.fooddelivery.customer.model.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// No delete yet: orders will reference addresses, so whether deleting an address is a
// hard delete, a soft delete, or safe because orders keep their own address snapshot
// must be decided together with the orders table.
public interface AddressRepository {

    // Inserts a new address, or updates the existing one with the same id
    Address save(Address address);

    Optional<Address> findById(UUID addressId);

    // Default address first, then oldest first
    List<Address> findByCustomerId(UUID customerId);
}
