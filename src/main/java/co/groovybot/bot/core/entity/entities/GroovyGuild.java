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

package co.groovybot.bot.core.entity.entities;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.entity.DatabaseEntitiy;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.json.JSONArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@Getter
@ToString
public class GroovyGuild extends DatabaseEntitiy {

    private Integer volume = 100;
    private String prefix = GroovyBot.getInstance().getConfig().getJSONObject("settings").getString("prefix");
    private boolean djMode = false;
    private boolean announceSongs = true;
    private boolean autoLeave = true;
    private boolean autoPause = false;
    private boolean preventDups = false;
    private boolean deleteMessages = true;
    private boolean searchPlay = false;
    private long autoJoinChannelId;
    private long djRole = 0;
    private JSONArray blacklistedChannels = new JSONArray();
    @Getter
    private long botChannel = 0;

    public GroovyGuild(Long entityId) throws Exception {
        super(entityId);
        try (Connection connection = getConnection()) {

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE id = ?");
            ps.setLong(1, entityId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                volume = rs.getInt("volume");
                prefix = rs.getString("prefix");
                djMode = rs.getBoolean("dj_mode");
                announceSongs = rs.getBoolean("announce_songs");
                autoLeave = rs.getBoolean("auto_leave");
                autoPause = rs.getBoolean("auto_pause");
                autoJoinChannelId = rs.getLong("auto_join_channel");
                botChannel = rs.getLong("commands_channel");
                blacklistedChannels = new JSONArray(rs.getString("blacklisted_channels"));
                preventDups = rs.getBoolean("prevent_dups");
                deleteMessages = rs.getBoolean("delete_messages");
            } else {
                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO guilds (id, prefix, volume, dj_mode, announce_songs, auto_leave, blacklisted_channels, commands_channel, auto_pause, prevent_dups, delete_messages) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                insertStatement.setLong(1, entityId);
                insertStatement.setString(2, prefix);
                insertStatement.setInt(3, volume);
                insertStatement.setBoolean(4, djMode);
                insertStatement.setBoolean(5, announceSongs);
                insertStatement.setBoolean(6, autoLeave);
                insertStatement.setString(7, blacklistedChannels.toString());
                insertStatement.setLong(8, botChannel);
                insertStatement.setBoolean(9, autoPause);
                insertStatement.setBoolean(10, preventDups);
                insertStatement.setBoolean(11, deleteMessages);
                insertStatement.execute();
            }
        }
    }

    @Override
    public void updateInDatabase() throws Exception {
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE guilds SET volume = ?, prefix = ?, dj_mode = ?, announce_songs = ?, auto_leave = ?, commands_channel = ?, blacklisted_channels = ?, auto_pause = ?, auto_join_channel = ?, prevent_dups = ?, delete_messages = ? WHERE id = ?");
            ps.setInt(1, volume);
            ps.setString(2, prefix);
            ps.setBoolean(3, djMode);
            ps.setBoolean(4, announceSongs);
            ps.setBoolean(5, autoLeave);
            ps.setLong(6, botChannel);
            ps.setString(7, blacklistedChannels.toString());
            ps.setBoolean(8, autoPause);
            ps.setLong(9, autoJoinChannelId);
            ps.setBoolean(10, preventDups);
            ps.setBoolean(11, deleteMessages);
            ps.setLong(12, entityId);
            ps.execute();
        }
    }

    public void setDeleteMessages(boolean deleteMessage) {
        this.deleteMessages = deleteMessage;
        update();
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
        update();
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        update();
    }

    public void setDjMode(boolean djMode) {
        this.djMode = djMode;
        update();
    }

    public void setAnnounceSongs(boolean announceSongs) {
        this.announceSongs = announceSongs;
        update();
    }

    public void setAutoLeave(boolean autoLeave) {
        this.autoLeave = autoLeave;
        update();
    }

    public void setAutoPause(boolean autopause) {
        this.autoPause = autopause;
        update();
    }

    public void blacklistChannel(long channelId) {
        blacklistedChannels.put(channelId);
        update();
    }

    public void setBotChannel(long botChannel) {
        this.botChannel = botChannel;
        update();
    }

    public void setAutoJoinChannelId(long autoJoinChannelId) {
        this.autoJoinChannelId = autoJoinChannelId;
        update();
    }

    public void setSearchPlay(boolean searchPlay) {
        this.searchPlay = searchPlay;
        update();
    }

    public void setDjRole(long djRoleId) {
        this.djRole = djRoleId;
        update();
    }

    public VoiceChannel getAutoJoinChannel() {
        return GroovyBot.getInstance().getShardManager().getGuildById(entityId).getVoiceChannelById(autoJoinChannelId);
    }

    public void setAutoJoinChannel(VoiceChannel channel) {
        setAutoJoinChannelId(channel.getIdLong());
    }

    public boolean hasAutoJoinChannel() {
        return autoJoinChannelId != 0L;
    }

    public void unBlacklistChannel(long channelId) {
        if (isChannelBlacklisted(channelId)) {
            final List<Object> channels = blacklistedChannels.toList();
            channels.remove(channelId);
            blacklistedChannels = new JSONArray(channels);
            update();
        }
    }

    public void setPreventDups(boolean preventDups) {
        this.preventDups = preventDups;
        update();
    }

    public boolean hasBlacklistedChannels() {
        return !blacklistedChannels.isEmpty();
    }

    public boolean hasCommandsChannel() {
        return botChannel != 0;
    }

    public List<Object> getBlacklistedChannels() {
        return blacklistedChannels.toList();
    }

    public boolean isChannelBlacklisted(long channelId) {
        return blacklistedChannels.toList().contains(channelId);
    }

    private void update() {
        GroovyBot.getInstance().getGuildCache().update(this);
    }

    public void reset() {
        blacklistedChannels = new JSONArray();
        autoJoinChannelId = 0;
        botChannel = 0;
        volume = 100;
        prefix = GroovyBot.getInstance().getConfig().getJSONObject("settings").getString("prefix");
        djMode = false;
        announceSongs = true;
        autoLeave = true;
        autoPause = false;
        preventDups = false;
        deleteMessages = false;
    }
}
