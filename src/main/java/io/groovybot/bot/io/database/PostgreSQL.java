package io.groovybot.bot.io.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import io.groovybot.bot.GroovyBot;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class PostgreSQL {

    private final List<PostgreSQLDatabase> defaults;
    @Getter
    private Connection connection;

    public PostgreSQL() {
        log.info("[Database] Connecting ...");
        defaults = new ArrayList<>();
        JSONObject configuration = GroovyBot.getInstance().getConfig().getJSONObject("db");
        HikariConfig hikariConfig = new HikariConfig();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            log.error("[Database] Error while connecting to database!", e);
        }
        hikariConfig.setJdbcUrl(String.format("jdbc:postgresql://%s:%d/%s", configuration.getString("host"), configuration.getInt("port"), configuration.getString("database")));
        hikariConfig.setUsername(configuration.getString("username"));
        hikariConfig.setPassword(configuration.getString("password"));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("serverTimezone", "Europe/Berlin");
        hikariConfig.addDataSourceProperty("useSSL", "false");

        try {
            connection = new HikariDataSource(hikariConfig).getConnection();
        } catch (SQLException | HikariPool.PoolInitializationException e) {
            log.error("[Database] Error while connecting to database!", e);
        }

        if (connection != null)
            log.info("[Database] Connected!");
    }

    public void createDatabases() {
        defaults.forEach(postgreSQLDatabase -> {
            try {
                connection.prepareStatement(postgreSQLDatabase.getCreateStatement()).execute();
            } catch (SQLException e) {
                log.error("[Database] Error while creating databases!", e);
            }
        });
    }

    public void addDefault(PostgreSQLDatabase database) {
        defaults.add(database);
    }

    public interface PostgreSQLDatabase {

        String getCreateStatement();

    }

}
