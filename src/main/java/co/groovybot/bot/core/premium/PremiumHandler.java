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

package co.groovybot.bot.core.premium;

import co.groovybot.bot.util.PremiumUtil;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class PremiumHandler {

    private Map<Long, Tier> patrons = new HashMap<>();
    private Connection connection;

    public void initializePatrons(Guild guild, Connection connection) {
        log.info("[PremiumHandler] Initializing PremiumHandler ...");

        this.connection = connection;
        AtomicInteger patronCount = new AtomicInteger();
        guild.getMembers().forEach(member -> {
            Tier tier = PremiumUtil.getTier(member, guild);
            if (tier != Tier.NONE) {
                patronCount.addAndGet(1);
                patrons.put(member.getUser().getIdLong(), tier);
            }
        });

        try {
            PreparedStatement remove = connection.prepareStatement("TRUNCATE premium");
            remove.execute();

            PreparedStatement premium = connection.prepareStatement("SELECT * FROM premium");
            ResultSet premiumSet = premium.executeQuery();

            while (premiumSet.next()) {
                Tier givenTier = patrons.get(premiumSet.getLong("user_id"));

                if (!patrons.containsKey(premiumSet.getLong("user_id")))
                    removePatron(premiumSet.getLong("user_id"));
                else if (!givenTier.toString().equals(premiumSet.getString("type")))
                    updatePatron(premiumSet.getLong("user_id"), givenTier);

                patrons.remove(premiumSet.getLong("user_id"));
            }

            patrons.forEach(this::addPatron);
        } catch (SQLException e) {
            log.error("[PremiumHandler] Error while initializing Patrons!", e);
        }

        log.info("[PremiumHandler] Successfully initialized PremiumHandler!");
    }

    public void addPatron(long id, Tier tier) {
        try {
            PreparedStatement insert = connection.prepareStatement("INSERT INTO premium (user_id, type) VALUES (?, ?)");
            insert.setLong(1, id);
            insert.setString(2, tier.toString());
            insert.execute();
        } catch (SQLException e) {
            log.error("[PremiumHandler] Error while inserting patron!", e);
        }
    }

    public void updatePatron(long id, Tier tier) {
        try {
            PreparedStatement insert = connection.prepareStatement("UPDATE premium SET type = ? WHERE user_id = ?");
            insert.setString(1, tier.toString());
            insert.setLong(2, id);
            insert.execute();
        } catch (SQLException e) {
            log.error("[PremiumHandler] Error while updating patron!", e);
        }
    }

    public void removePatron(long id) {
        try {
            PreparedStatement remove = connection.prepareStatement("DELETE FROM premium WHERE user_id = ?");
            remove.setLong(1, id);
            remove.execute();
        } catch (SQLException e) {
            log.error("[PremiumHandler] Error while removing patron!", e);
        }
    }
}
