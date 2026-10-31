package com.prasad.fooddelivery.common.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

// The PostgreSQL driver maps TIMESTAMPTZ to OffsetDateTime, not Instant.
// These helpers keep that conversion in one place for every repository.
public final class JdbcTimestamps {

    private JdbcTimestamps() {
    }

    public static OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant.atOffset(ZoneOffset.UTC);
    }

    public static Instant getInstant(ResultSet resultSet, String column) throws SQLException {
        OffsetDateTime value = resultSet.getObject(column, OffsetDateTime.class);
        return value == null ? null : value.toInstant();
    }
}
