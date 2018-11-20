package io.groovybot.bot.core.entity;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.command.permission.UserPermissions;
import io.groovybot.bot.core.premium.Tier;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

@Log4j2
@Getter
public class User extends DatabaseEntitiy {

    private long expiration = 0;
    private long again = 0;
    private Locale locale = GroovyBot.getInstance().getTranslationManager().getDefaultLocale().getLocale();

    public User(Long entityId) throws Exception {
        super(entityId);
        try (Connection connection = getConnection()) {
            PreparedStatement user = connection.prepareStatement("SELECT * FROM users WHERE user_id = ?");
            user.setLong(1, entityId);

            ResultSet userResult = user.executeQuery();
            if (userResult.next())
                locale = Locale.forLanguageTag(userResult.getString("locale").replace("_", "-"));
            else {
                PreparedStatement insertUser = connection.prepareStatement("INSERT INTO users (user_id, locale) VALUES (?, ?)");
                insertUser.setLong(1, entityId);
                insertUser.setString(2, locale.toLanguageTag().replace("-", "_"));
                insertUser.execute();
            }

            PreparedStatement voted = connection.prepareStatement("SELECT * FROM voted WHERE user_id = ?");
            voted.setLong(1, entityId);

            ResultSet votedResult = voted.executeQuery();
            if (!votedResult.next()) {
                PreparedStatement insertVoted = connection.prepareStatement("INSERT INTO voted (user_id, expiration, again) VALUES (?, ?, ?)");
                insertVoted.setLong(1, entityId);
                insertVoted.setLong(2, expiration);
                insertVoted.setLong(3, again);
                insertVoted.execute();
            }
        }
    }

    @Override
    public void updateInDatabase() throws Exception {
        try (Connection connection = getConnection()) {
            PreparedStatement user = connection.prepareStatement("UPDATE users SET locale = ? WHERE user_id = ?");
            user.setString(1, locale.toLanguageTag().replace("-", "_"));
            user.setLong(2, entityId);
            user.execute();

            PreparedStatement voted = connection.prepareStatement("UPDATE voted SET expiration = ?, again = ? WHERE user_id = ?");
            voted.setLong(1, expiration);
            voted.setLong(2, again);
            voted.setLong(3, entityId);
            voted.execute();
        }
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        update();
    }

    public void setVoted(long expiration, long again) {
        this.expiration = expiration;
        this.again = again;
        update();
    }

    public boolean hasVoted() {
        return getPermissions().hasVoted();
    }

    public boolean hasAlreadyVoted() {
        try {
            Connection connection = this.getConnection();
            PreparedStatement voted = connection.prepareStatement("SELECT expiration FROM voted WHERE user_id = ?");
            voted.setLong(1, entityId);

            ResultSet votedResult = voted.executeQuery();
            if (votedResult.next())
                if (votedResult.getLong("expiration") > System.currentTimeMillis()) return true;
        } catch (SQLException e) {
            log.error("[User] Error while checking for hasVoted!", e);
        }
        return false;
    }

    public boolean isAbleToVote() {
        try {
            Connection connection = this.getConnection();
            PreparedStatement voted = connection.prepareStatement("SELECT again FROM voted WHERE user_id = ?");
            voted.setLong(1, entityId);

            ResultSet votedResult = voted.executeQuery();
            if (votedResult.next())
                if (votedResult.getLong("again") < System.currentTimeMillis()) return true;
        } catch (SQLException e) {
            log.error("[User] Error while checking for hasVoted!", e);
        }
        return false;
    }

    public boolean isFriend() {
        try {
            Connection connection = this.getConnection();
            PreparedStatement user = connection.prepareStatement("SELECT * FROM users WHERE user_id = ?");
            user.setLong(1, entityId);

            ResultSet userResult = user.executeQuery();
            if (userResult.next())
                return userResult.getBoolean("friend");
        } catch (SQLException e) {
            log.error("[User] Error while checking for isFriend!", e);
        }
        return false;
    }

    public void setFriend(boolean friend) {
        try {
            Connection connection = this.getConnection();
            PreparedStatement user = connection.prepareStatement("UPDATE users SET friend = ? WHERE user_id = ?");
            user.setBoolean(1, friend);
            user.setLong(2, entityId);
            user.execute();
        } catch (SQLException e) {
            log.error("[User] Error while checking for isFriend!", e);
        }
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
