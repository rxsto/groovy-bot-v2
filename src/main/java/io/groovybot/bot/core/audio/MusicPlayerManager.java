package io.groovybot.bot.core.audio;


import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.command.CommandEvent;
import lavalink.client.LavalinkUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.json.JSONArray;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class MusicPlayerManager {

    @Getter
    private Map<Long, MusicPlayer> playerStorage = new HashMap<>();

    public MusicPlayer getPlayer(Guild guild, TextChannel channel) {
        if (playerStorage.containsKey(guild.getIdLong()))
            return playerStorage.get(guild.getIdLong());
        MusicPlayer player = new MusicPlayer(guild, channel, GroovyBot.getInstance().getYoutubeClient());
        playerStorage.put(guild.getIdLong(), player);
        return player;
    }

    public MusicPlayer getPlayer(CommandEvent event) {
        return getPlayer(event.getGuild(), event.getChannel());
    }

    public int getPlayingServers() {
        return LavalinkManager.countPlayers();
    }

    public void update(Guild guild, MusicPlayer player) {
        playerStorage.replace(guild.getIdLong(), player);
    }

    public void initPlayers() throws SQLException, IOException {
        int initializedPlayersCount = 0;

        try (Connection connection = GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection()) {

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM queues");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Guild guild = GroovyBot.getInstance().getShardManager().getGuildById(rs.getLong("guild_id"));
                if (guild == null)
                    continue;
                TextChannel textChannel = guild.getTextChannelById(rs.getLong("text_channel_id"));
                VoiceChannel voiceChannel = guild.getVoiceChannelById(rs.getLong("channel_id"));

                MusicPlayer player = getPlayer(guild, textChannel);

                player.connect(voiceChannel);
                player.setVolume(rs.getInt("volume"));
                player.play(LavalinkUtil.toAudioTrack(rs.getString("current_track")));
                player.seekTo(rs.getLong("current_position"));
                for (Object track : new JSONArray(rs.getString("queue"))) {
                    player.queueTrack(LavalinkUtil.toAudioTrack(track.toString()), false, false);
                }

                initializedPlayersCount++;
            }

            PreparedStatement delPs = connection.prepareStatement("DELETE FROM queues");
            delPs.execute();
        }

        log.info(String.format("[MusicPlayerManager] Successfully initialized %s %s!", initializedPlayersCount, initializedPlayersCount == 1 ? "MusicPlayer" : "MusicPlayers"));
    }
}
