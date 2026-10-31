package com.prasad.fooddelivery.customer.repository;

import com.prasad.fooddelivery.common.db.PostgresTestDatabase;
import com.prasad.fooddelivery.common.exception.DataAccessException;
import com.prasad.fooddelivery.customer.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// Integration test: runs against a real PostgreSQL started in Docker by Testcontainers,
// with the schema created by the same Flyway migrations used in production.
class JdbcCustomerRepositoryTest {

    private CustomerRepository repository;

    @BeforeEach
    void setUp() {
        PostgresTestDatabase.clearAllTables();
        repository = new JdbcCustomerRepository(PostgresTestDatabase.dataSource());
    }

    private Customer createCustomer(String email, String phoneNumber) {
        return new Customer("Prasad", "Kumar", email, phoneNumber, "hashedPassword123");
    }

    @Test
    void shouldSaveAndFindCustomerById() {
        Customer customer = createCustomer("prasad@example.com", "9876543210");

        repository.save(customer);
        Optional<Customer> found = repository.findById(customer.getId());

        assertTrue(found.isPresent());
        assertEquals(customer.getId(), found.get().getId());
        assertEquals("Prasad", found.get().getFirstName());
        assertEquals("Kumar", found.get().getLastName());
        assertEquals("prasad@example.com", found.get().getEmail());
        assertEquals("9876543210", found.get().getPhoneNumber());
        assertEquals("hashedPassword123", found.get().getPasswordHash());
        assertFalse(found.get().isActive());
    }

    @Test
    void shouldKeepStoredStateAndTimestamps() {
        Instant createdAt = Instant.parse("2026-01-10T10:15:30Z");
        Instant updatedAt = Instant.parse("2026-02-01T08:00:00Z");
        Customer customer = Customer.restore(
                UUID.randomUUID(), "Prasad", "Kumar", "prasad@example.com",
                "9876543210", "hashedPassword123", true, createdAt, updatedAt
        );

        repository.save(customer);
        Customer found = repository.findById(customer.getId()).orElseThrow();

        assertTrue(found.isActive());
        assertEquals(createdAt, found.getCreatedAt());
        assertEquals(updatedAt, found.getUpdatedAt());
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFound() {
        assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
    }

    @Test
    void shouldUpdateExistingCustomerWhenSavedAgain() {
        Customer customer = createCustomer("prasad@example.com", "9876543210");
        repository.save(customer);

        customer.updateName("Rahul", "Sharma");
        customer.activate();
        repository.save(customer);

        Customer found = repository.findById(customer.getId()).orElseThrow();
        assertEquals("Rahul", found.getFirstName());
        assertEquals("Sharma", found.getLastName());
        assertTrue(found.isActive());
    }

    @Test
    void shouldRejectDuplicateEmailIgnoringCase() {
        repository.save(createCustomer("prasad@example.com", "9876543210"));

        assertThrows(DataAccessException.class, () ->
                repository.save(createCustomer("PRASAD@example.com", "9000000000"))
        );
    }

    @Test
    void shouldRejectDuplicatePhoneNumber() {
        repository.save(createCustomer("prasad@example.com", "9876543210"));

        assertThrows(DataAccessException.class, () ->
                repository.save(createCustomer("rahul@example.com", "9876543210"))
        );
    }

    @Test
    void shouldRejectNullCustomer() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void shouldRejectNullId() {
        assertThrows(IllegalArgumentException.class, () -> repository.findById(null));
    }
}
