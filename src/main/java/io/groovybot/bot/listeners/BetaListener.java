package io.groovybot.bot.listeners;

import io.groovybot.bot.GroovyBot;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j2
public class BetaListener {

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onGuildJoin(GuildJoinEvent event) {
        try {
            final GroovyBot instance = GroovyBot.getInstance();
            PreparedStatement ps = instance.getPostgreSQL().getConnection().prepareStatement("SELECT * FROM beta WHERE user_id = ?");
            ps.setLong(1, event.getGuild().getOwnerIdLong());
            if (!ps.executeQuery().next() && instance.isDebugMode())
                event.getGuild().leave().queue();
        } catch (SQLException e) {
            log.error("Error while joining beta guild", e);
        }
    }
}
