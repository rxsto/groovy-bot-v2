package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import io.groovybot.bot.GroovyBot;
import lavalink.client.io.jda.JdaLavalink;
import lombok.extern.log4j.Log4j;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.json.JSONObject;

import java.net.URI;

@Log4j
public class LavalinkManager {

    private JdaLavalink lavalink;
    private AudioPlayerManager audioPlayerManager;
    private GroovyBot groovyBot;

    public LavalinkManager(GroovyBot groovyBot) {
        log.info("[LAVALINK] Connecting to lavalink nodes");
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
        groovyBot.getConfig().getJSONArray("lavalink").forEach(link -> {
            JSONObject object = ((JSONObject) link);
            lavalink.addNode(URI.create(object.getString("uri")), object.getString("password"));
        });
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onEvent(Event event) {
        lavalink.onEvent(event);
    }
}
