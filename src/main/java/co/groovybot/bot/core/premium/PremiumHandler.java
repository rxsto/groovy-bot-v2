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

@Log4j2
public class PremiumHandler {

    private Map<Long, Tier> patrons = new HashMap<>();
    private Connection connection;

    public void initializePatrons(Guild guild, Connection connection) {
        this.connection = connection;
        guild.getMembers().forEach(member -> {
            Tier tier = PremiumUtil.getTier(member, guild);
            if (tier != Tier.NONE)
                patrons.put(member.getUser().getIdLong(), tier);
        });

        try {
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

        log.info("[PremiumHandler] Successfully initialized Patrons!");
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
