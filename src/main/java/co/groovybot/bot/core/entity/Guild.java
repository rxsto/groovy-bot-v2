package co.groovybot.bot.core.entity;

import co.groovybot.bot.GroovyBot;
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
public class Guild extends DatabaseEntitiy {

    private Integer volume = 100;
    private String prefix = GroovyBot.getInstance().getConfig().getJSONObject("settings").getString("prefix");
    private boolean djMode = false;
    private boolean announceSongs = true;
    private boolean autoLeave = true;
    private boolean autoPause = false;
    private boolean preventDups = false;
    private long autoJoinChannelId;
    private JSONArray blacklistedChannels = new JSONArray();
    @Getter
    private long botChannel = 0;

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
                autoLeave = rs.getBoolean("auto_leave");
                autoPause = rs.getBoolean("auto_pause");
                autoJoinChannelId = rs.getLong("auto_join_channel");
                botChannel = rs.getLong("commands_channel");
                blacklistedChannels = new JSONArray(rs.getString("blacklisted_channels"));
                preventDups = rs.getBoolean("prevent_dups");
            } else {
                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO guilds (id, prefix, volume, dj_mode, announce_songs, auto_leave, blacklisted_channels, commands_channel, auto_pause, prevent_dups) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
                insertStatement.execute();
            }
        }
    }

    @Override
    public void updateInDatabase() throws Exception {
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE guilds SET volume = ?, prefix = ?, dj_mode = ?, announce_songs = ?, auto_leave = ?, commands_channel = ?, blacklisted_channels = ?, auto_pause = ?, auto_join_channel = ?, prevent_dups = ? WHERE id = ?");
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
            ps.setLong(11, entityId);
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

    public void setAutoJoinChannel(VoiceChannel channel) {
        setAutoJoinChannelId(channel.getIdLong());
    }

    public VoiceChannel getAutoJoinChannel() {
        return GroovyBot.getInstance().getShardManager().getGuildById(entityId).getVoiceChannelById(autoJoinChannelId);
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
}
