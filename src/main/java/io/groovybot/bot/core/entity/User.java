package io.groovybot.bot.core.entity;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.command.permission.UserPermissions;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Map;

@Getter
public class User extends DatabaseEntitiy {

    private boolean tierOne = false;
    private boolean tierTwo = false;
    private Locale locale = GroovyBot.getInstance().getTranslationManager().getDefaultLocale().getLocale();

    public User(Long entityId) throws Exception {
        super(entityId);
        try (Connection connection = getConnection()) {
            PreparedStatement userStatement = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            userStatement.setLong(1, entityId);
            ResultSet userResult = userStatement.executeQuery();
            if (userResult.next())
                locale = Locale.forLanguageTag(userResult.getString("locale").replace("_", "-"));
            else {
                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO users (id, locale) VALUES (?, ?)");
                insertStatement.setLong(1, entityId);
                insertStatement.setString(2, locale.toLanguageTag().replace("-", "_"));
                insertStatement.execute();
            }
        }
    }

    @Override
    public void updateInDatabase() throws Exception {
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE users SET locale = ? WHERE id = ?");
            ps.setString(1, locale.toLanguageTag().replace("-", "_"));
            ps.setLong(2, entityId);
            ps.execute();
            PreparedStatement ps2 = connection.prepareStatement("INSERT INTO premium (user_id, type, \"check\")" +
                    "VALUES (?, ?, FALSE)");
            ps2.setLong(1, entityId);
            ps2.setLong(2, tierTwo ? 2 : tierOne ? 1 : 0);
            if (tierTwo || tierOne)
                ps2.execute();
        }
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        update();
    }

    public void setPremium(int type) {
        if (type == 1)
            tierOne = true;
        else if (type == 2)
            tierTwo = true;
        else {
            tierOne = false;
            tierTwo = false;
        }
        update();
    }

    public UserPermissions getPermissions() {
        return new UserPermissions(this, GroovyBot.getInstance());
    }

    public Map<String, Playlist> getPlaylists() {
        return GroovyBot.getInstance().getPlaylistManager().getPlaylist(entityId);
    }

    private void update() {
        GroovyBot.getInstance().getUserCache().update(this);
    }
}
