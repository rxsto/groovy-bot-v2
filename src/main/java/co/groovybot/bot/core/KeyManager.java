/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergeij Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
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

package co.groovybot.bot.core;

import co.groovybot.bot.core.entity.Key;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
public class KeyManager {

    private final HikariDataSource dataSource;

    public boolean keyExists(String key) {
        try {
            return Objects.requireNonNull(getKeyInfo(key)).executeQuery().next();
        } catch (SQLException e) {
            log.error("[KeyManager] Error occurred while retrieving key", e);
        }
        return false;
    }

    public Key getKey(String key) {
        try {
            ResultSet rs = Objects.requireNonNull(Objects.requireNonNull(getKeyInfo(key)).executeQuery());
            if (rs.next())
                return new Key(Key.KeyType.valueOf(rs.getString("type")), UUID.fromString(rs.getString("key")), dataSource);
        } catch (SQLException e) {
            log.error("[KeyManager] Error occurred while retrieving key", e);
        }
        return null;
    }

    public UUID generateKey(Key.KeyType type) {
        Key key = new Key(type);
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO \"keys\" (type, \"key\") VALUES (?,?)");
            ps.setString(1, type.toString());
            ps.setString(2, key.getKey().toString());
            ps.execute();
        } catch (SQLException e) {
            log.error("[KeyManager] Error occurred while retrieving key", e);
        }
        return key.getKey();
    }

    private PreparedStatement getKeyInfo(String key) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM \"keys\" WHERE \"key\" = ?");
            ps.setString(1, key);
            return ps;
        } catch (SQLException e) {
            log.error("[KeyManager] Error occurred while retrieving key", e);
            return null;
        }
    }
}
