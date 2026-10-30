package com.prasad.fooddelivery.customer.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void shouldCreateCustomer() {

        Customer customer = new Customer(
                "Prasad",
                "Kumar",
                "prasad@example.com",
                "9876543210",
                "hashedPassword123"
        );

        assertNotNull(customer.getId());
        assertEquals("Prasad", customer.getFirstName());
        assertEquals("Kumar", customer.getLastName());
        assertEquals("prasad@example.com", customer.getEmail());
        assertEquals("9876543210", customer.getPhoneNumber());
        assertFalse(customer.isActive());
    }

    @Test
    void shouldUpdateCustomerName() {

        Customer customer = new Customer(
                "Prasad",
                "Kumar",
                "prasad@example.com",
                "9876543210",
                "hashedPassword123"
        );

        customer.updateName("Rahul", "Sharma");

        assertEquals("Rahul", customer.getFirstName());
        assertEquals("Sharma", customer.getLastName());
    }

    @Test
    void shouldActivateCustomer() {

        Customer customer = new Customer(
                "Prasad",
                "Kumar",
                "prasad@example.com",
                "9876543210",
                "hashedPassword123"
        );

        customer.activate();

        assertTrue(customer.isActive());
    }
}