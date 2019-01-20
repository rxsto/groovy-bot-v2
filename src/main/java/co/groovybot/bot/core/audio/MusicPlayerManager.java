/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package co.groovybot.bot.core.audio;


import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.util.EmbedUtil;
import co.groovybot.bot.util.SafeMessage;
import lavalink.client.LavalinkUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.Permission;
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

    public MusicPlayer getExistingPlayer(Guild guild) {
        if (playerStorage.containsKey(guild.getIdLong()))
            return playerStorage.get(guild.getIdLong());
        return null;
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

    public void initPlayers(boolean noJoin) throws SQLException, IOException {
        int initializedPlayersCount = 0;

        log.info("[MusicPlayerManager] Initializing MusicPlayers ...");

        try (Connection connection = GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM queues");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Guild guild = GroovyBot.getInstance().getShardManager().getGuildById(rs.getLong("guild_id"));

                if (guild == null)
                    continue;

                TextChannel textChannel = guild.getTextChannelById(rs.getLong("text_channel_id"));

                if (textChannel == null) {
                    textChannel = guild.getTextChannels().stream().filter(TextChannel::canTalk).filter(channel -> channel.getName().toLowerCase().contains("bot") || channel.getName().toLowerCase().contains("command") || channel.getName().toLowerCase().contains("music")).findFirst().orElse(guild.getTextChannels().stream().filter(TextChannel::canTalk).findFirst().orElse(null));

                    if (textChannel == null)
                        continue;
                }

                VoiceChannel voiceChannel = guild.getVoiceChannelById(rs.getLong("channel_id"));

                if (voiceChannel == null)
                    continue;

                MusicPlayer player = getPlayer(guild, textChannel);

                if (!guild.getSelfMember().hasPermission(voiceChannel, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
                    SafeMessage.sendMessage(textChannel, EmbedUtil.error("Error", "Groovy is not able to join this voicechannel."));
                    continue;
                }

                if (voiceChannel.getUserLimit() != 0 && !guild.getSelfMember().hasPermission(voiceChannel, Permission.ADMINISTRATOR) && !guild.getSelfMember().hasPermission(voiceChannel, Permission.VOICE_MOVE_OTHERS) && voiceChannel.getMembers().size() >= voiceChannel.getUserLimit()) {
                    SafeMessage.sendMessage(textChannel, EmbedUtil.error("Error", "Groovy is not able to join this voicechannel as its limit is already reached."));
                    continue;
                }

                player.getScheduler().setLoopqueue(rs.getBoolean("loop_queue"));
                player.getScheduler().setLoop(rs.getBoolean("loop"));
                player.getScheduler().setShuffle(rs.getBoolean("shuffle"));
                player.getScheduler().setAutoPlay(rs.getBoolean("auto_play"));

                player.setBassboost(rs.getString("bassboost"));
                player.setSkipVotes(rs.getInt("skip_votes"));

                player.connect(voiceChannel);

                player.setVolume(rs.getInt("volume"));

                player.play(LavalinkUtil.toAudioTrack(rs.getString("current_track")));
                player.seekTo(rs.getLong("current_position"));

                for (Object track : new JSONArray(rs.getString("queue")))
                    player.queueTrack(LavalinkUtil.toAudioTrack(track.toString()), false, false);

                initializedPlayersCount++;
            }

            PreparedStatement delPs = connection.prepareStatement("DELETE FROM queues");
            delPs.execute();
        }

        if (!noJoin) {
            MusicPlayer groovyPlayer = getPlayer(GroovyBot.getInstance().getShardManager().getGuildById(403882830225997825L), GroovyBot.getInstance().getShardManager().getTextChannelById(486765014976561159L));
            groovyPlayer.connect(GroovyBot.getInstance().getShardManager().getVoiceChannelById(GroovyBot.getInstance().getConfig().getJSONObject("settings").getString("voice")));
        }

        log.info(String.format("[MusicPlayerManager] Successfully initialized %s %s!", initializedPlayersCount, initializedPlayersCount == 1 ? "MusicPlayer" : "MusicPlayers"));
    }

    public void updateAllPlayers() {
        playerStorage.forEach((id, player) -> player.update());
    }
}
