package com.prasad.fooddelivery.customer.model;

import java.time.Instant;
import java.util.UUID;

public class Customer {
    private final UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    public Customer(String firstName, String lastName,
                    String email, String phoneNumber,
                    String passwordHash) {

        validateName(firstName, lastName);

        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash is required");
        }

        this.id = UUID.randomUUID();

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;

        this.active = false;

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    // Used only by restore() to rebuild an existing customer
    private Customer(UUID id, String firstName, String lastName,
                     String email, String phoneNumber, String passwordHash,
                     boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Rebuilds a customer that already exists (e.g. loaded from the database).
    // Unlike the public constructor, it keeps the stored id, state and timestamps.
    public static Customer restore(UUID id, String firstName, String lastName,
                                   String email, String phoneNumber, String passwordHash,
                                   boolean active, Instant createdAt, Instant updatedAt) {
        if (id == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        return new Customer(id, firstName, lastName, email, phoneNumber,
                passwordHash, active, createdAt, updatedAt);
    }

    private void validateName(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }

    public void updateName(String firstName, String lastName) {
        validateName(firstName, lastName);

        this.firstName = firstName;
        this.lastName = lastName;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
