package io.groovybot.bot.listeners;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.io.WebsocketConnection;
import io.groovybot.bot.util.NameThreadFactory;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@Log4j2
public class WebsiteStatsListener implements Runnable {

    private final ScheduledExecutorService scheduler;

    public WebsiteStatsListener() {
        scheduler = Executors.newScheduledThreadPool(1, new NameThreadFactory("websiteStats"));
        scheduler.scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);
    }

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

    @Override
    public void run() {
        sendHeartBeat();
    }

    private void sendHeartBeat() {
        GroovyBot.getInstance().getWebsocket().send(WebsocketConnection.parseMessage("heartbeat", new JSONObject().put("state", "alive")).toString());
    }

    private void updateStats() {
        if (GroovyBot.getInstance().getWebsocket().isClosed())
            return;
        log.debug("[Websocket] Sending Heartbeat to Server!");
        GroovyBot.getInstance().getWebsocket().send(WebsocketConnection.parseMessage("poststats", WebsocketConnection.parseStats(GroovyBot.getInstance().getLavalinkManager().countPlayers(), GroovyBot.getInstance().getShardManager().getGuilds().size(), GroovyBot.getInstance().getShardManager().getUsers().size())).toString());
    }
}
