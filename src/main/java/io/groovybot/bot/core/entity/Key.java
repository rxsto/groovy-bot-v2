package io.groovybot.bot.core.entity;

import com.zaxxer.hikari.HikariDataSource;
import io.groovybot.bot.GroovyBot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
public class Key {

    @Getter
    private final KeyType type;
    @Getter
    private final UUID key;
    private final HikariDataSource dataSource;

    public Key(KeyType type) {
        this.type = type;
        this.key = UUID.randomUUID();
        this.dataSource = GroovyBot.getInstance().getPostgreSQL().getDataSource();
    }


    public void redeem(User user) throws Exception {
        switch (type) {
            case BETA:
                try (Connection connection = dataSource.getConnection()) {
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO beta (user_id) VALUES (?)");
                    ps.setLong(1, user.getIdLong());
                    ps.execute();
                } catch (SQLException e) {
                    log.error("[KeyManager] Error while redeeming beta key", e);
                }
                break;
            case TIER_ONE:
                setPremium(user, 1);
                break;
            case TIER_TWO:
                setPremium(user, 2);
                break;
            default:
                //Do nothing
                break;
        }
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM keys WHERE \"key\" = ?");
            deleteStatement.setString(1, key.toString());
            deleteStatement.execute();
        } catch (SQLException e) {
            log.error("[KeyManager] Error while deleting key", e);

        }
    }

    private void setPremium(User user, int type) {
        final io.groovybot.bot.core.entity.User user1 = EntityProvider.getUser(user.getIdLong());
    }

    @RequiredArgsConstructor
    public enum KeyType {

        BETA("beta"),
        TIER_ONE("Premium tier one"),
        TIER_TWO("Premium tier two");

        @Getter
        private final String displayName;
    }
}
