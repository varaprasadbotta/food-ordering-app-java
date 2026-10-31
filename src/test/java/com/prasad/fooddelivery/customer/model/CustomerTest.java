package com.prasad.fooddelivery.customer.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

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

    @Test
    void shouldExposePasswordHashForPersistence() {

        Customer customer = new Customer(
                "Prasad",
                "Kumar",
                "prasad@example.com",
                "9876543210",
                "hashedPassword123"
        );

        assertEquals("hashedPassword123", customer.getPasswordHash());
    }

    @Test
    void shouldRejectBlankFirstName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Customer(" ", "Kumar", "prasad@example.com", "9876543210", "hash")
        );
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () ->
                new Customer("Prasad", "Kumar", "not-an-email", "9876543210", "hash")
        );
    }

    @Test
    void shouldRejectMissingPhoneNumber() {
        assertThrows(IllegalArgumentException.class, () ->
                new Customer("Prasad", "Kumar", "prasad@example.com", null, "hash")
        );
    }

    @Test
    void shouldRejectMissingPasswordHash() {
        assertThrows(IllegalArgumentException.class, () ->
                new Customer("Prasad", "Kumar", "prasad@example.com", "9876543210", "")
        );
    }

    @Test
    void shouldRejectBlankNameOnUpdate() {

        Customer customer = new Customer(
                "Prasad",
                "Kumar",
                "prasad@example.com",
                "9876543210",
                "hashedPassword123"
        );

        assertThrows(IllegalArgumentException.class, () ->
                customer.updateName("Rahul", "")
        );
        assertEquals("Kumar", customer.getLastName());
    }

    @Test
    void shouldRestoreExistingCustomerWithStoredState() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-10T10:15:30Z");
        Instant updatedAt = Instant.parse("2026-02-01T08:00:00Z");

        Customer customer = Customer.restore(
                id, "Prasad", "Kumar", "prasad@example.com", "9876543210",
                "hashedPassword123", true, createdAt, updatedAt
        );

        assertEquals(id, customer.getId());
        assertTrue(customer.isActive());
        assertEquals(createdAt, customer.getCreatedAt());
        assertEquals(updatedAt, customer.getUpdatedAt());
    }

    @Test
    void shouldRejectRestoreWithoutId() {
        Instant now = Instant.now();

        assertThrows(IllegalArgumentException.class, () ->
                Customer.restore(null, "Prasad", "Kumar", "prasad@example.com",
                        "9876543210", "hash", true, now, now)
        );
    }
}