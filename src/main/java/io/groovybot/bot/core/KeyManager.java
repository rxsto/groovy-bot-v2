package io.groovybot.bot.core;

import io.groovybot.bot.core.entity.Key;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

@Log4j
@RequiredArgsConstructor
public class KeyManager {

    private final Connection connection;

    public boolean keyExists(String key) {
        System.out.println(key);
        try {
            return Objects.requireNonNull(getKeyStatement(key)).executeQuery().next();
        } catch (SQLException e) {
            log.error("[KeyManager] Error occurred while retrieving key", e);
        }
        return false;
    }

    public Key getKey(String key) {
        try {
            ResultSet rs = Objects.requireNonNull(getKeyStatement(key)).executeQuery();
            if (rs.next())
                return new Key(Key.KeyType.valueOf(rs.getString("type")), UUID.fromString(rs.getString("key")), connection);
        } catch (SQLException e) {
            log.error("[KeyManager] Error occurred while retrieving key", e);
        }
        return null;
    }

    public UUID generateKey(Key.KeyType type) {
        Key key = new Key(type);
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO \"keys\" (type, \"key\") VALUES (?,?)");
            ps.setString(1, type.toString());
            ps.setString(2, key.getKey().toString());
            ps.execute();
        } catch (SQLException e) {
            log.error("[KeyManager] Error occurred while retrieving key", e);
        }
        return key.getKey();
    }

    private PreparedStatement getKeyStatement(String key) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM \"keys\" WHERE \"key\" = ?");
            ps.setString(1, key);
            return ps;
        } catch (SQLException e) {
            log.error("[KeyManager] Error occurred while retrieving key", e);
            return null;
        }
    }
}
