package com.prasad.fooddelivery.order.service;

import com.prasad.fooddelivery.customer.repository.CustomerRepository;

public class OrderService {

    private final CustomerRepository customerRepository;

    public OrderService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
}