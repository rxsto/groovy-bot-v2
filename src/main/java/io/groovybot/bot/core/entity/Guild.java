package io.groovybot.bot.core.entity;

import io.groovybot.bot.GroovyBot;
import lombok.Getter;
import lombok.ToString;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Getter
@ToString
public class Guild extends DatabaseEntitiy {

    private Integer volume = 100;
    private String prefix = "g!";
    private boolean djMode = false;

    public Guild(Long entityId) throws Exception {
        super(entityId);
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM guilds WHERE id = ?");
        ps.setLong(1, entityId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            volume = rs.getInt("volume");
            prefix = rs.getString("prefix");
            djMode = rs.getBoolean("dj_mode");
        } else {
            PreparedStatement insertStatement = getConnection().prepareStatement("INSERT INTO guilds (id, prefix, volume, dj_mode) VALUES (?, ?, ?, ?)");
            insertStatement.setLong(1, entityId);
            insertStatement.setString(2, prefix);
            insertStatement.setInt(3, volume);
            insertStatement.setBoolean(4, djMode);
            ps.execute();
        }
    }

    @Override
    public void updateInDatabase() throws Exception {
        PreparedStatement ps = getConnection().prepareStatement("UPDATE guilds SET volume = ?, prefix = ?, dj_mode = ?");
        ps.setInt(1, volume);
        ps.setString(2, prefix);
        ps.setBoolean(3, djMode);
        ps.execute();
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

    private void update() {
        GroovyBot.getInstance().getGuildCache().update(this);
    }
}
