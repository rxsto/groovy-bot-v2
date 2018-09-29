package io.groovybot.bot.core.entity;

import io.groovybot.bot.GroovyBot;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

@RequiredArgsConstructor
public class Key {

    private final KeyType type;
    private final UUID key;
    private final Connection connection;

    public Key(KeyType type) {
        this.type = type;
        this.key = UUID.randomUUID();
        this.connection = GroovyBot.getInstance().getPostgreSQL().getConnection();
    }


    public void redeem(User user) throws Exception {
        switch (type) {
            case BETA:
                PreparedStatement ps = connection.prepareStatement("INSERT INTO beta (user_id) VALUE ?");
                ps.setLong(1, user.getIdLong());
                ps.execute();
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
        PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM keys WHERE `key` = ?");
        deleteStatement.setString(1, key.toString());
        deleteStatement.execute();
    }

    private void setPremium(User user, int type) throws Exception {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO premium (user_id, type, `check`) VALUES (?, ?, ?)");
        ps.setLong(1, user.getIdLong());
        ps.setInt(2, type);
        ps.setBoolean(3, false);
        ps.execute();
    }

    @RequiredArgsConstructor
    public enum KeyType {

        BETA("beta"),
        TIER_ONE("Premium tier one"),
        TIER_TWO("Premium tier two");

        private final String displayname;
    }
}
