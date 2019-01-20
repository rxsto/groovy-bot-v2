/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

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

        log.info("[Database] Successfully connected to Database!");
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
