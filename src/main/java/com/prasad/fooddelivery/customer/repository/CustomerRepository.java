package com.prasad.fooddelivery.customer.repository;

import com.prasad.fooddelivery.customer.model.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    // Inserts a new customer, or updates the existing one with the same id
    Customer save(Customer customer);

    Optional<Customer> findById(UUID customerId);
}
