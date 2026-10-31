package com.prasad.fooddelivery.restaurant.repository;

import com.prasad.fooddelivery.common.exception.DataAccessException;
import com.prasad.fooddelivery.restaurant.model.DietaryType;
import com.prasad.fooddelivery.restaurant.model.MenuCategory;
import com.prasad.fooddelivery.restaurant.model.MenuItem;

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

public class JdbcMenuItemRepository implements MenuItemRepository {

    // restaurant_id is not updated: a menu item never moves to another restaurant
    private static final String UPSERT_SQL = """
            INSERT INTO menu_items (
                id, restaurant_id, name, description, price, category, dietary_type,
                image_url, available, recommended, created_at, updated_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                name         = EXCLUDED.name,
                description  = EXCLUDED.description,
                price        = EXCLUDED.price,
                category     = EXCLUDED.category,
                dietary_type = EXCLUDED.dietary_type,
                image_url    = EXCLUDED.image_url,
                available    = EXCLUDED.available,
                recommended  = EXCLUDED.recommended,
                updated_at   = EXCLUDED.updated_at
            """;

    private static final String SELECT_COLUMNS = """
            SELECT id, restaurant_id, name, description, price, category, dietary_type,
                   image_url, available, recommended, created_at, updated_at
            FROM menu_items
            """;

    private static final String FIND_BY_ID_SQL = SELECT_COLUMNS + "WHERE id = ?";

    // Stable order (oldest first). Grouping by category for display is the service's job;
    // ORDER BY category would sort the enum names alphabetically, not in menu order.
    private static final String FIND_BY_RESTAURANT_ID_SQL =
            SELECT_COLUMNS + "WHERE restaurant_id = ? ORDER BY created_at, id";

    private final DataSource dataSource;

    public JdbcMenuItemRepository(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource is required");
        }
        this.dataSource = dataSource;
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item is required");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPSERT_SQL)) {

            statement.setObject(1, menuItem.getId());
            statement.setObject(2, menuItem.getRestaurantId());
            statement.setString(3, menuItem.getName());
            statement.setString(4, menuItem.getDescription());
            statement.setBigDecimal(5, menuItem.getPrice());
            statement.setString(6, menuItem.getCategory().name());
            statement.setString(7, menuItem.getDietaryType().name());
            statement.setString(8, menuItem.getImageUrl());
            statement.setBoolean(9, menuItem.isAvailable());
            statement.setBoolean(10, menuItem.isRecommended());
            statement.setObject(11, toOffsetDateTime(menuItem.getCreatedAt()));
            statement.setObject(12, toOffsetDateTime(menuItem.getUpdatedAt()));

            statement.executeUpdate();
            return menuItem;

        } catch (SQLException e) {
            throw new DataAccessException("Failed to save menu item " + menuItem.getId(), e);
        }
    }

    @Override
    public Optional<MenuItem> findById(UUID menuItemId) {
        if (menuItemId == null) {
            throw new IllegalArgumentException("Menu item ID is required");
        }

        List<MenuItem> found = query(FIND_BY_ID_SQL, menuItemId, "Failed to find menu item " + menuItemId);
        return found.stream().findFirst();
    }

    @Override
    public List<MenuItem> findByRestaurantId(UUID restaurantId) {
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID is required");
        }

        return query(FIND_BY_RESTAURANT_ID_SQL, restaurantId,
                "Failed to find menu items for restaurant " + restaurantId);
    }

    private List<MenuItem> query(String sql, UUID parameter, String errorMessage) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, parameter);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<MenuItem> menuItems = new ArrayList<>();
                while (resultSet.next()) {
                    menuItems.add(mapRow(resultSet));
                }
                return menuItems;
            }

        } catch (SQLException e) {
            throw new DataAccessException(errorMessage, e);
        }
    }

    private MenuItem mapRow(ResultSet resultSet) throws SQLException {
        return MenuItem.restore(
                resultSet.getObject("id", UUID.class),
                resultSet.getObject("restaurant_id", UUID.class),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getBigDecimal("price"),
                MenuCategory.valueOf(resultSet.getString("category")),
                DietaryType.valueOf(resultSet.getString("dietary_type")),
                resultSet.getString("image_url"),
                resultSet.getBoolean("available"),
                resultSet.getBoolean("recommended"),
                getInstant(resultSet, "created_at"),
                getInstant(resultSet, "updated_at")
        );
    }
}
