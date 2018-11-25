package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.audio.spotify.source.SpotifySourceManager;
import lavalink.client.io.jda.JdaLavalink;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.json.JSONArray;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class LavalinkManager {

    @Getter
    private static JdaLavalink lavalink;
    @Getter
    private AudioPlayerManager audioPlayerManager;
    private GroovyBot groovyBot;

    public LavalinkManager(GroovyBot groovyBot) {
        log.info("[LavalinkManager] Connecting to Nodes ...");
        this.groovyBot = groovyBot;
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        audioPlayerManager.registerSourceManager(new SpotifySourceManager(groovyBot.getSpotifyClient(), new AudioTrackFactory()));
        audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
        audioPlayerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
        audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
        audioPlayerManager.registerSourceManager(new LocalAudioSourceManager());
    }

    public static int countPlayers() {
        if (lavalink == null)
            return 0;
        AtomicInteger playingPlayers = new AtomicInteger();
        lavalink.getNodes().forEach(
                node -> {
                    if (node.getStats() != null)
                        playingPlayers.addAndGet(node.getStats().getPlayingPlayers());
                }
        );
        return playingPlayers.get();
    }

    public void initialize() {
        log.info("[LavalinkManager] Initializing Lavalink and trying to connect to Nodes ...");

        lavalink = new JdaLavalink(
                groovyBot.getShardManager().getApplicationInfo().complete().getId(),
                groovyBot.getShardManager().getShardsTotal(),
                groovyBot.getShardManager()::getShardById
        );

        if (GroovyBot.getInstance().isConfigNodes()) {
            JSONArray rootArray = GroovyBot.getInstance().getConfig().getJSONArray("lavalink_nodes");
            for (int i = 0; i < rootArray.length(); i++) {
                String[] array = rootArray.getString(i).split("&&");
                lavalink.addNode(array[0], URI.create(array[1]), array[2]);
            }
        } else {
            try (Connection connection = groovyBot.getPostgreSQL().getDataSource().getConnection()) {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM lavalink");
                ResultSet rs = ps.executeQuery();
                while (rs.next())
                    lavalink.addNode(URI.create(rs.getString("uri")), rs.getString("password"));
            } catch (SQLException e) {
                log.error("[LavalinkManager] Error while loading Lavalink!");
                return;
            }
        }

        log.info(String.format("[LavalinkManager] Successfully initialized Lavalink with %s %s!", lavalink.getNodes().size(), lavalink.getNodes().size() == 1 ? "Node" : "Nodes"));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onEvent(Event event) {
        if (lavalink != null)
            lavalink.onEvent(event);
    }
}
