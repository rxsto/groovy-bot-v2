package io.groovybot.bot.core.entity;

import io.groovybot.bot.GroovyBot;
import lombok.Getter;
import lombok.ToString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Getter
@ToString
public class Guild extends DatabaseEntitiy {

    private Integer volume = 100;
    private String prefix = "g!";
    private boolean djMode = false;
    private boolean announceSongs = true;

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
            } else {
                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO guilds (id, prefix, volume, dj_mode) VALUES (?, ?, ?, ?)");
                insertStatement.setLong(1, entityId);
                insertStatement.setString(2, prefix);
                insertStatement.setInt(3, volume);
                insertStatement.setBoolean(4, djMode);
                ps.execute();
            }
        }
    }

    @Override
    public void updateInDatabase() throws Exception {
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE guilds SET volume = ?, prefix = ?, dj_mode = ?, announce_songs = ? WHERE id = ?");
            ps.setInt(1, volume);
            ps.setString(2, prefix);
            ps.setBoolean(3, djMode);
            ps.setBoolean(4, announceSongs);
            ps.setLong(5, entityId);
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

    private void update() {
        GroovyBot.getInstance().getGuildCache().update(this);
    }
}
