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

package co.groovybot.bot.core.entity;

import co.groovybot.bot.GroovyBot;
import com.zaxxer.hikari.HikariDataSource;
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
        final co.groovybot.bot.core.entity.User user1 = EntityProvider.getUser(user.getIdLong());
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
