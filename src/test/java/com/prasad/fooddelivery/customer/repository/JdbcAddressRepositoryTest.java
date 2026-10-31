package com.prasad.fooddelivery.customer.repository;

import com.prasad.fooddelivery.common.db.PostgresTestDatabase;
import com.prasad.fooddelivery.common.exception.DataAccessException;
import com.prasad.fooddelivery.customer.model.Address;
import com.prasad.fooddelivery.customer.model.AddressLabel;
import com.prasad.fooddelivery.customer.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JdbcAddressRepositoryTest {

    private AddressRepository repository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        PostgresTestDatabase.clearAllTables();
        repository = new JdbcAddressRepository(PostgresTestDatabase.dataSource());

        // customer_addresses.customer_id is a foreign key, so the customer must exist first
        customer = new JdbcCustomerRepository(PostgresTestDatabase.dataSource()).save(
                new Customer("Prasad", "Kumar", "prasad@example.com", "9876543210", "hash")
        );
    }

    private Address createAddress(UUID customerId, AddressLabel label) {
        return new Address(
                customerId,
                label,
                "Flat 101",
                "MG Road",
                "Near Metro Station",
                "Bengaluru",
                "Karnataka",
                "560001",
                new BigDecimal("12.971600"),
                new BigDecimal("77.594600")
        );
    }

    @Test
    void shouldSaveAndFindAddressById() {
        Address address = createAddress(customer.getId(), AddressLabel.HOME);

        repository.save(address);
        Address found = repository.findById(address.getId()).orElseThrow();

        assertEquals(address.getId(), found.getId());
        assertEquals(customer.getId(), found.getCustomerId());
        assertEquals(AddressLabel.HOME, found.getLabel());
        assertEquals("Flat 101", found.getHouseNumber());
        assertEquals("MG Road", found.getStreet());
        assertEquals("Near Metro Station", found.getLandmark());
        assertEquals("Bengaluru", found.getCity());
        assertEquals("Karnataka", found.getState());
        assertEquals("560001", found.getPostalCode());
        assertEquals(new BigDecimal("12.971600"), found.getLatitude());
        assertEquals(new BigDecimal("77.594600"), found.getLongitude());
        assertFalse(found.isDefaultAddress());
    }

    @Test
    void shouldUpdateAddressWhenSavedAgain() {
        Address address = createAddress(customer.getId(), AddressLabel.HOME);
        repository.save(address);

        address.updateAddress(AddressLabel.WORK, "Tower B", "Outer Ring Road", null,
                "Bengaluru", "Karnataka", "560103", null, null);
        repository.save(address);

        Address found = repository.findById(address.getId()).orElseThrow();
        assertEquals(AddressLabel.WORK, found.getLabel());
        assertEquals("Outer Ring Road", found.getStreet());
        assertEquals("560103", found.getPostalCode());
        assertNull(found.getLandmark());
        assertNull(found.getLatitude());
    }

    @Test
    void shouldListCustomerAddressesWithDefaultFirst() {
        Address home = createAddress(customer.getId(), AddressLabel.HOME);
        Address work = createAddress(customer.getId(), AddressLabel.WORK);
        work.setAsDefault();

        repository.save(home);
        repository.save(work);

        List<Address> addresses = repository.findByCustomerId(customer.getId());

        assertEquals(2, addresses.size());
        assertEquals(work.getId(), addresses.get(0).getId());
        assertTrue(addresses.get(0).isDefaultAddress());
    }

    @Test
    void shouldRejectSecondDefaultAddressForSameCustomer() {
        Address home = createAddress(customer.getId(), AddressLabel.HOME);
        home.setAsDefault();
        repository.save(home);

        Address work = createAddress(customer.getId(), AddressLabel.WORK);
        work.setAsDefault();

        // Enforced by the partial unique index, not by Java code
        assertThrows(DataAccessException.class, () -> repository.save(work));
    }

    @Test
    void shouldAllowSwitchingDefaultWhenOldOneIsClearedFirst() {
        Address home = createAddress(customer.getId(), AddressLabel.HOME);
        home.setAsDefault();
        repository.save(home);

        Address work = createAddress(customer.getId(), AddressLabel.WORK);
        repository.save(work);

        // Order matters: clear the old default before setting the new one.
        // AddressService will do both inside one transaction.
        home.removeAsDefault();
        repository.save(home);
        work.setAsDefault();
        repository.save(work);

        List<Address> addresses = repository.findByCustomerId(customer.getId());
        assertEquals(work.getId(), addresses.get(0).getId());
        assertEquals(1, addresses.stream().filter(Address::isDefaultAddress).count());
    }

    @Test
    void shouldRejectAddressForUnknownCustomer() {
        Address address = createAddress(UUID.randomUUID(), AddressLabel.HOME);

        assertThrows(DataAccessException.class, () -> repository.save(address));
    }

    @Test
    void shouldReturnEmptyWhenAddressNotFound() {
        assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
        assertTrue(repository.findByCustomerId(customer.getId()).isEmpty());
    }

    @Test
    void shouldRejectNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
        assertThrows(IllegalArgumentException.class, () -> repository.findById(null));
        assertThrows(IllegalArgumentException.class, () -> repository.findByCustomerId(null));
    }
}
