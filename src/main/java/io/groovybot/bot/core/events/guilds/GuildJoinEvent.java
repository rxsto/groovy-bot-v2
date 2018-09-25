package io.groovybot.bot.core.events.guilds;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

public class GuildJoinEvent extends net.dv8tion.jda.core.events.guild.GuildJoinEvent {
    public GuildJoinEvent(JDA api, long responseNumber, Guild guild) {
        super(api, responseNumber, guild);
    }
}
