package io.groovybot.bot.core.entity;

import io.groovybot.bot.GroovyBot;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@Getter
@ToString
public class Guild extends DatabaseEntitiy {

    private Integer volume = 100;
    private String prefix = GroovyBot.getInstance().getConfig().getJSONObject("settings").getString("prefix");
    private boolean djMode = false;
    private boolean announceSongs = true;
    private JSONArray blacklistedChannels = new JSONArray();
    @Getter
    private TextChannel botChannel = null;

    public Guild(Long entityId) throws Exception {
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
                if (rs.getObject("commands_channel") != null)
                    botChannel = GroovyBot.getInstance().getShardManager().getTextChannelById(rs.getLong("commands_channel"));
                else botChannel = null;
                blacklistedChannels = new JSONArray(rs.getString("blacklisted_channels"));
            } else {
                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO guilds (id, prefix, volume, dj_mode, blacklisted_channels) VALUES (?, ?, ?, ?, ?)");
                insertStatement.setLong(1, entityId);
                insertStatement.setString(2, prefix);
                insertStatement.setInt(3, volume);
                insertStatement.setBoolean(4, djMode);
                insertStatement.setString(5, blacklistedChannels.toString());
                insertStatement.execute();
            }
        }
    }

    @Override
    public void updateInDatabase() throws Exception {
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE guilds SET volume = ?, prefix = ?, dj_mode = ?, announce_songs = ?, commands_channel = ?, blacklisted_channels = ? WHERE id = ?");
            ps.setInt(1, volume);
            ps.setString(2, prefix);
            ps.setBoolean(3, djMode);
            ps.setBoolean(4, announceSongs);
            if (hasCommandsChannel()) ps.setLong(5, botChannel.getIdLong());
            else ps.setObject(5, null);
            ps.setString(6, blacklistedChannels.toString());
            ps.setLong(7, entityId);
            ps.execute();
        }
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

    public void blacklistChannel(long channelId) {
        blacklistedChannels.put(channelId);
        update();
    }

    public void setBotChannel(TextChannel botChannel) {
        this.botChannel = botChannel;
        update();
    }

    public void unBlacklistChannel(long channelId) {
        if (isChannelBlacklisted(channelId)) {
            final List<Object> channels = blacklistedChannels.toList();
            channels.remove(channelId);
            blacklistedChannels = new JSONArray(channels);
            update();
        }
    }

    public boolean hasBlacklistedChannels() {
        return !blacklistedChannels.isEmpty();
    }

    public boolean hasCommandsChannel() {
        return botChannel != null;
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
}
