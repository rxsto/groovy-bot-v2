package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import io.groovybot.bot.GroovyBot;
import lavalink.client.io.jda.JdaLavalink;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.json.JSONObject;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Log4j
public class LavalinkManager {

    @Getter
    private JdaLavalink lavalink;
    private AudioPlayerManager audioPlayerManager;
    private GroovyBot groovyBot;

    public LavalinkManager(GroovyBot groovyBot) {
        log.info("[Lavalink] Connecting to lavalink nodes");
        this.groovyBot = groovyBot;
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
        audioPlayerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
    }

    public void initialize() {
        lavalink = new JdaLavalink(
                groovyBot.getShardManager().getApplicationInfo().complete().getId(),
                groovyBot.getShardManager().getShardsTotal(),
                groovyBot.getShardManager()::getShardById
        );
        try {
            PreparedStatement ps = groovyBot.getPostgreSQL().getConnection().prepareStatement("SELECT * FROM lavalink_nodes");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lavalink.addNode(URI.create(rs.getString("uri")), rs.getString("password"));
        } catch (SQLException e) {
            log.error("[Lavalink] Error while loading lavalink");
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onEvent(Event event) {
        lavalink.onEvent(event);
    }
}
