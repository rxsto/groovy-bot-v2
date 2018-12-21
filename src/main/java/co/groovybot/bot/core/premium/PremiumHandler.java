package co.groovybot.bot.core.premium;

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
            Tier tier = Tier.NONE;
            if (member.getRoles().contains(guild.getRoleById(1234L)))
                tier = Tier.ONE;
            if (member.getRoles().contains(guild.getRoleById(5678L)))
                tier = Tier.TWO;
            if (member.getRoles().contains(guild.getRoleById(9101L)))
                tier = Tier.THREE;
            if (tier == Tier.NONE)
                return;
            patrons.put(member.getUser().getIdLong(), tier);
        });

        try {
            PreparedStatement premium = connection.prepareStatement("SELECT * FROM premium");
            ResultSet premiumSet = premium.executeQuery();

            while (premiumSet.next()) {
                if (!patrons.containsKey(premiumSet.getLong("user_id")))
                    premiumSet.deleteRow();
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
