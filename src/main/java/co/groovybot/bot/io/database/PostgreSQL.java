package co.groovybot.bot.io.database;

import co.groovybot.bot.GroovyBot;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class PostgreSQL implements Closeable {

    private final List<PostgreSQLDatabase> defaults;
    @Getter
    private HikariDataSource dataSource;

    public PostgreSQL() {
        log.info("[Database] Connecting to Database ...");
        defaults = new ArrayList<>();
        JSONObject configuration = GroovyBot.getInstance().getConfig().getJSONObject("db");
        HikariConfig hikariConfig = new HikariConfig();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            log.error("[Database] Error while connecting to database!", e);
        }
        hikariConfig.setJdbcUrl(String.format("jdbc:postgresql://%s:%d/%s?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false", configuration.getString("host"), configuration.getInt("port"), configuration.getString("database")));
        hikariConfig.setUsername(configuration.getString("username"));
        hikariConfig.setPassword(configuration.getString("password"));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.setMinimumIdle(15);

        try {
            dataSource = new HikariDataSource(hikariConfig);
        } catch (HikariPool.PoolInitializationException e) {
            log.error("[Database] Error while connecting to Database!", e);
            return;
        }

        log.info("[Database] Connected!");
    }

    public void createDatabases() {
        defaults.forEach(postgreSQLDatabase -> {
            try (Connection connection = dataSource.getConnection()) {
                connection.prepareStatement(postgreSQLDatabase.getCreateStatement()).execute();
            } catch (SQLException e) {
                log.error("[Database] Error while creating databases!", e);
            }
        });
    }

    public void addDefault(PostgreSQLDatabase database) {
        defaults.add(database);
    }

    @Override
    public void close() throws IOException {
        dataSource.close();
    }


    public interface PostgreSQLDatabase {
        String getCreateStatement();
    }
}
