package com.prasad.fooddelivery.customer.repository;

import com.prasad.fooddelivery.common.exception.DataAccessException;
import com.prasad.fooddelivery.customer.model.Customer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static com.prasad.fooddelivery.common.db.JdbcTimestamps.getInstant;
import static com.prasad.fooddelivery.common.db.JdbcTimestamps.toOffsetDateTime;

public class JdbcCustomerRepository implements CustomerRepository {

    // ON CONFLICT makes save() an "upsert": insert a new row, or update the row with the same id.
    // created_at is deliberately not in the UPDATE list - it must never change.
    private static final String UPSERT_SQL = """
            INSERT INTO customers (
                id, first_name, last_name, email, phone_number,
                password_hash, active, created_at, updated_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                first_name    = EXCLUDED.first_name,
                last_name     = EXCLUDED.last_name,
                email         = EXCLUDED.email,
                phone_number  = EXCLUDED.phone_number,
                password_hash = EXCLUDED.password_hash,
                active        = EXCLUDED.active,
                updated_at    = EXCLUDED.updated_at
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT id, first_name, last_name, email, phone_number,
                   password_hash, active, created_at, updated_at
            FROM customers
            WHERE id = ?
            """;

    private final DataSource dataSource;

    public JdbcCustomerRepository(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource is required");
        }
        this.dataSource = dataSource;
    }

    @Override
    public Customer save(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPSERT_SQL)) {

            statement.setObject(1, customer.getId());
            statement.setString(2, customer.getFirstName());
            statement.setString(3, customer.getLastName());
            statement.setString(4, customer.getEmail());
            statement.setString(5, customer.getPhoneNumber());
            statement.setString(6, customer.getPasswordHash());
            statement.setBoolean(7, customer.isActive());
            statement.setObject(8, toOffsetDateTime(customer.getCreatedAt()));
            statement.setObject(9, toOffsetDateTime(customer.getUpdatedAt()));

            statement.executeUpdate();
            return customer;

        } catch (SQLException e) {
            throw new DataAccessException("Failed to save customer " + customer.getId(), e);
        }
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setObject(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(resultSet));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to find customer " + customerId, e);
        }
    }

    // Row mapper: turns one database row back into a Customer using restore(),
    // so the stored id, active flag and timestamps are kept.
    private Customer mapRow(ResultSet resultSet) throws SQLException {
        return Customer.restore(
                resultSet.getObject("id", UUID.class),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("email"),
                resultSet.getString("phone_number"),
                resultSet.getString("password_hash"),
                resultSet.getBoolean("active"),
                getInstant(resultSet, "created_at"),
                getInstant(resultSet, "updated_at")
        );
    }
}
