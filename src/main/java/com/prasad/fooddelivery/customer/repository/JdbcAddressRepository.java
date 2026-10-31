package com.prasad.fooddelivery.customer.repository;

import com.prasad.fooddelivery.common.exception.DataAccessException;
import com.prasad.fooddelivery.customer.model.Address;
import com.prasad.fooddelivery.customer.model.AddressLabel;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.prasad.fooddelivery.common.db.JdbcTimestamps.getInstant;
import static com.prasad.fooddelivery.common.db.JdbcTimestamps.toOffsetDateTime;

public class JdbcAddressRepository implements AddressRepository {

    // customer_id is not updated: an address never moves to another customer
    private static final String UPSERT_SQL = """
            INSERT INTO customer_addresses (
                id, customer_id, label, house_number, street, landmark, city, state,
                postal_code, latitude, longitude, is_default, created_at, updated_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                label        = EXCLUDED.label,
                house_number = EXCLUDED.house_number,
                street       = EXCLUDED.street,
                landmark     = EXCLUDED.landmark,
                city         = EXCLUDED.city,
                state        = EXCLUDED.state,
                postal_code  = EXCLUDED.postal_code,
                latitude     = EXCLUDED.latitude,
                longitude    = EXCLUDED.longitude,
                is_default   = EXCLUDED.is_default,
                updated_at   = EXCLUDED.updated_at
            """;

    private static final String SELECT_COLUMNS = """
            SELECT id, customer_id, label, house_number, street, landmark, city, state,
                   postal_code, latitude, longitude, is_default, created_at, updated_at
            FROM customer_addresses
            """;

    private static final String FIND_BY_ID_SQL = SELECT_COLUMNS + "WHERE id = ?";

    private static final String FIND_BY_CUSTOMER_ID_SQL =
            SELECT_COLUMNS + "WHERE customer_id = ? ORDER BY is_default DESC, created_at, id";

    private final DataSource dataSource;

    public JdbcAddressRepository(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource is required");
        }
        this.dataSource = dataSource;
    }

    @Override
    public Address save(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address is required");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPSERT_SQL)) {

            statement.setObject(1, address.getId());
            statement.setObject(2, address.getCustomerId());
            statement.setString(3, address.getLabel().name());
            statement.setString(4, address.getHouseNumber());
            statement.setString(5, address.getStreet());
            statement.setString(6, address.getLandmark());
            statement.setString(7, address.getCity());
            statement.setString(8, address.getState());
            statement.setString(9, address.getPostalCode());
            statement.setBigDecimal(10, address.getLatitude());
            statement.setBigDecimal(11, address.getLongitude());
            statement.setBoolean(12, address.isDefaultAddress());
            statement.setObject(13, toOffsetDateTime(address.getCreatedAt()));
            statement.setObject(14, toOffsetDateTime(address.getUpdatedAt()));

            statement.executeUpdate();
            return address;

        } catch (SQLException e) {
            throw new DataAccessException("Failed to save address " + address.getId(), e);
        }
    }

    @Override
    public Optional<Address> findById(UUID addressId) {
        if (addressId == null) {
            throw new IllegalArgumentException("Address ID is required");
        }

        List<Address> found = query(FIND_BY_ID_SQL, addressId, "Failed to find address " + addressId);
        return found.stream().findFirst();
    }

    @Override
    public List<Address> findByCustomerId(UUID customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        return query(FIND_BY_CUSTOMER_ID_SQL, customerId,
                "Failed to find addresses for customer " + customerId);
    }

    private List<Address> query(String sql, UUID parameter, String errorMessage) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, parameter);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Address> addresses = new ArrayList<>();
                while (resultSet.next()) {
                    addresses.add(mapRow(resultSet));
                }
                return addresses;
            }

        } catch (SQLException e) {
            throw new DataAccessException(errorMessage, e);
        }
    }

    private Address mapRow(ResultSet resultSet) throws SQLException {
        return Address.restore(
                resultSet.getObject("id", UUID.class),
                resultSet.getObject("customer_id", UUID.class),
                AddressLabel.valueOf(resultSet.getString("label")),
                resultSet.getString("house_number"),
                resultSet.getString("street"),
                resultSet.getString("landmark"),
                resultSet.getString("city"),
                resultSet.getString("state"),
                resultSet.getString("postal_code"),
                resultSet.getBigDecimal("latitude"),
                resultSet.getBigDecimal("longitude"),
                resultSet.getBoolean("is_default"),
                getInstant(resultSet, "created_at"),
                getInstant(resultSet, "updated_at")
        );
    }
}
