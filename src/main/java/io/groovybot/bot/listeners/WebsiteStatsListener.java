package io.groovybot.bot.listeners;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.audio.LavalinkManager;
import io.groovybot.bot.io.WebsocketConnection;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@SuppressWarnings("unused")
@Log4j2
public class WebsiteStatsListener {

    @SubscribeEvent
    private void onGuildJoin(GuildJoinEvent event) {
        updateStats();
    }

    @SubscribeEvent
    private void onGuildLeave(GuildLeaveEvent event) {
        updateStats();
    }

    @SubscribeEvent
    private void onMemberJoin(GuildMemberJoinEvent event) {
        updateStats();
    }

    @SubscribeEvent
    private void onMemberLeave(GuildMemberLeaveEvent event) {
        updateStats();
    }

    private void updateStats() {
        if (GroovyBot.getInstance().getWebsocket() == null || GroovyBot.getInstance().getWebsocket().isClosed() || !GroovyBot.getInstance().getWebsocket().isOpen())
            return;
        GroovyBot.getInstance().getWebsocket().send(WebsocketConnection.parseMessage("bot", "poststats", WebsocketConnection.parseStats(LavalinkManager.countPlayers(), GroovyBot.getInstance().getShardManager().getGuilds().size(), GroovyBot.getInstance().getShardManager().getUsers().size())).toString());
    }
}
