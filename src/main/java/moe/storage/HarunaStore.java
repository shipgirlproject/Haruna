package moe.storage;

import moe.Haruna;
import moe.structure.HarunaUser;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;
import java.time.Instant;

public class HarunaStore {
    private final JdbcConnectionPool pool;
    private final Haruna haruna;

    public HarunaStore(Haruna haruna, String location) {
        haruna.HarunaLog.info("Connecting to the database....");
        pool = JdbcConnectionPool.create(
                "jdbc:h2:file:./db/HarunaStore;MULTI_THREADED=1",
                "",
                ""
        );
        try {
            try (Connection connection = pool.getConnection()) {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS HarunaStore(" +
                             "user TEXT NOT NULL," +
                             "timestamp INTEGER," +
                             "weekend TEXT NOT NULL," +
                             "UNIQUE(user)" +
                             ")"
                ).execute();
            }
        } catch (Exception error) {
            error.printStackTrace();
            haruna.formatTrace(error.getMessage(), error.getStackTrace());
            System.exit(0);
        }
        this.haruna = haruna;
        haruna.HarunaLog.info("Connected to the database @ " + location + "db\\HarunaStore");
    }

    public void save(String user, long timestamp, String weekend) throws Exception {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO HarunaStore(user, timestamp, weekend) " +
                    "VALUES(?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "timestamp = ?, weekend = ?"
            );
            statement.setString(0, user);
            statement.setLong(1, timestamp);
            statement.setString(2, weekend);
            statement.setLong(3, timestamp);
            statement.setString(4, weekend);
            statement.execute();
        }
    }

    public HarunaUser get(String user) throws Exception {
        HarunaUser data = null;
        try (Connection connection = pool.getConnection()) {
            try (ResultSet results = connection.prepareStatement(
                    "SELECT DISTINCT * FROM HarunaStore WHERE user = ?"
            ).executeQuery()) {
                while (results.next()) {
                    data = new HarunaUser();
                    data.user = results.getString("user");
                    data.timestamp = results.getLong("timestamp");
                    data.weekend = Boolean.parseBoolean(results.getString("weekend"));
                }
            }
        }
        return data;
    }

    public int clean() throws Exception {
        int cleaned;
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM HarunaStore WHERE timestamp <= ?");
            statement.setLong(0, Instant.now().toEpochMilli());
            cleaned = statement.executeUpdate();
        }
        haruna.HarunaLog.info("Cleaned " + cleaned + " saved entries in the database.");
        return cleaned;
    }
}