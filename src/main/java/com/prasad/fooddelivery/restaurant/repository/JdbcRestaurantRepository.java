package com.prasad.fooddelivery.restaurant.repository;

import com.prasad.fooddelivery.common.exception.DataAccessException;
import com.prasad.fooddelivery.restaurant.model.Restaurant;

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

public class JdbcRestaurantRepository implements RestaurantRepository {

    private static final String UPSERT_SQL = """
            INSERT INTO restaurants (
                id, name, description, phone_number, email,
                average_rating, total_ratings_count, pure_veg, active, accepting_orders,
                commission_percentage, created_at, updated_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                name                  = EXCLUDED.name,
                description           = EXCLUDED.description,
                phone_number          = EXCLUDED.phone_number,
                email                 = EXCLUDED.email,
                average_rating        = EXCLUDED.average_rating,
                total_ratings_count   = EXCLUDED.total_ratings_count,
                pure_veg              = EXCLUDED.pure_veg,
                active                = EXCLUDED.active,
                accepting_orders      = EXCLUDED.accepting_orders,
                commission_percentage = EXCLUDED.commission_percentage,
                updated_at            = EXCLUDED.updated_at
            """;

    private static final String DELETE_CUISINES_SQL =
            "DELETE FROM restaurant_cuisines WHERE restaurant_id = ?";

    private static final String INSERT_CUISINE_SQL =
            "INSERT INTO restaurant_cuisines (restaurant_id, cuisine, position) VALUES (?, ?, ?)";

    private static final String FIND_BY_ID_SQL = """
            SELECT id, name, description, phone_number, email,
                   average_rating, total_ratings_count, pure_veg, active, accepting_orders,
                   commission_percentage, created_at, updated_at
            FROM restaurants
            WHERE id = ?
            """;

    private static final String FIND_CUISINES_SQL =
            "SELECT cuisine FROM restaurant_cuisines WHERE restaurant_id = ? ORDER BY position";

    private final DataSource dataSource;

    public JdbcRestaurantRepository(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource is required");
        }
        this.dataSource = dataSource;
    }

    // A restaurant is stored in two tables. Both writes run in one transaction on one
    // connection, so a failure while saving cuisines never leaves a half-saved restaurant.
    @Override
    public Restaurant save(Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant is required");
        }

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try {
                upsertRestaurant(connection, restaurant);
                replaceCuisines(connection, restaurant);
                connection.commit();
                return restaurant;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to save restaurant " + restaurant.getId(), e);
        }
    }

    @Override
    public Optional<Restaurant> findById(UUID restaurantId) {
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID is required");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setObject(1, restaurantId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                List<String> cuisines = findCuisines(connection, restaurantId);
                return Optional.of(mapRow(resultSet, cuisines));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to find restaurant " + restaurantId, e);
        }
    }

    private void upsertRestaurant(Connection connection, Restaurant restaurant) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPSERT_SQL)) {
            statement.setObject(1, restaurant.getId());
            statement.setString(2, restaurant.getName());
            statement.setString(3, restaurant.getDescription());
            statement.setString(4, restaurant.getPhoneNumber());
            statement.setString(5, restaurant.getEmail());
            statement.setBigDecimal(6, restaurant.getAverageRating());
            statement.setInt(7, restaurant.getTotalRatingsCount());
            statement.setBoolean(8, restaurant.isPureVeg());
            statement.setBoolean(9, restaurant.isActive());
            statement.setBoolean(10, restaurant.isAcceptingOrders());
            statement.setBigDecimal(11, restaurant.getCommissionPercentage());
            statement.setObject(12, toOffsetDateTime(restaurant.getCreatedAt()));
            statement.setObject(13, toOffsetDateTime(restaurant.getUpdatedAt()));
            statement.executeUpdate();
        }
    }

    // Simplest correct way to sync a child list: delete the old rows, insert the current ones
    private void replaceCuisines(Connection connection, Restaurant restaurant) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement(DELETE_CUISINES_SQL)) {
            delete.setObject(1, restaurant.getId());
            delete.executeUpdate();
        }

        List<String> cuisines = restaurant.getCuisineTypes();
        if (cuisines.isEmpty()) {
            return;
        }

        try (PreparedStatement insert = connection.prepareStatement(INSERT_CUISINE_SQL)) {
            for (int position = 0; position < cuisines.size(); position++) {
                insert.setObject(1, restaurant.getId());
                insert.setString(2, cuisines.get(position));
                insert.setInt(3, position);
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }

    private List<String> findCuisines(Connection connection, UUID restaurantId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_CUISINES_SQL)) {
            statement.setObject(1, restaurantId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<String> cuisines = new ArrayList<>();
                while (resultSet.next()) {
                    cuisines.add(resultSet.getString("cuisine"));
                }
                return cuisines;
            }
        }
    }

    private Restaurant mapRow(ResultSet resultSet, List<String> cuisines) throws SQLException {
        return Restaurant.restore(
                resultSet.getObject("id", UUID.class),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("phone_number"),
                resultSet.getString("email"),
                cuisines,
                resultSet.getBigDecimal("average_rating"),
                resultSet.getInt("total_ratings_count"),
                resultSet.getBoolean("pure_veg"),
                resultSet.getBoolean("active"),
                resultSet.getBoolean("accepting_orders"),
                resultSet.getBigDecimal("commission_percentage"),
                getInstant(resultSet, "created_at"),
                getInstant(resultSet, "updated_at")
        );
    }
}
