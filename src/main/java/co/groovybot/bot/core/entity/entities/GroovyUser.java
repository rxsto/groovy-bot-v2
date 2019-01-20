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
import co.groovybot.bot.core.command.permission.UserPermissions;
import co.groovybot.bot.core.entity.DatabaseEntitiy;
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
public class GroovyUser extends DatabaseEntitiy {

    private long expiration = 0;
    private long again = 0;
    private Locale locale = GroovyBot.getInstance().getTranslationManager().getDefaultLocale().getLocale();

    public GroovyUser(Long entityId) throws Exception {
        super(entityId);
        try (Connection connection = getConnection()) {
            PreparedStatement user = connection.prepareStatement("SELECT * FROM users WHERE user_id = ?");
            user.setLong(1, entityId);

            ResultSet userResult = user.executeQuery();
            if (userResult.next()) {
                locale = Locale.forLanguageTag(userResult.getString("locale").replace("_", "-"));
                expiration = userResult.getLong("expiration");
                again = userResult.getLong("again");
            } else {
                PreparedStatement insertUser = connection.prepareStatement("INSERT INTO users (user_id, locale, expiration, again) VALUES (?, ?, ?, ?)");
                insertUser.setLong(1, entityId);
                insertUser.setString(2, locale.toLanguageTag().replace("-", "_"));
                insertUser.setLong(3, expiration);
                insertUser.setLong(4, again);
                insertUser.execute();
            }
        }
    }

    @Override
    public void updateInDatabase() throws Exception {
        try (Connection connection = getConnection()) {
            PreparedStatement user = connection.prepareStatement("UPDATE users SET locale = ?, expiration = ?, again = ? WHERE user_id = ?");
            user.setString(1, locale.toLanguageTag().replace("-", "_"));
            user.setLong(2, expiration);
            user.setLong(3, again);
            user.setLong(4, entityId);
            user.execute();
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
            PreparedStatement voted = connection.prepareStatement("SELECT expiration FROM users WHERE user_id = ?");
            voted.setLong(1, entityId);

            ResultSet votedResult = voted.executeQuery();
            if (votedResult.next())
                if (votedResult.getLong("expiration") > System.currentTimeMillis()) return true;
        } catch (SQLException e) {
            log.error("[GroovyUser] Error while checking for hasVoted!", e);
        }
        return false;
    }

    public boolean isAbleToVote() {
        try {
            Connection connection = this.getConnection();
            PreparedStatement voted = connection.prepareStatement("SELECT again FROM users WHERE user_id = ?");
            voted.setLong(1, entityId);

            ResultSet votedResult = voted.executeQuery();
            if (votedResult.next())
                if (votedResult.getLong("again") < System.currentTimeMillis()) return true;
        } catch (SQLException e) {
            log.error("[GroovyUser] Error while checking for hasVoted!", e);
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
            log.error("[GroovyUser] Error while checking for isFriend!", e);
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
            log.error("[GroovyUser] Error while checking for isFriend!", e);
        }
    }

    public UserPermissions getPermissions() {
        return new UserPermissions(this, GroovyBot.getInstance());
    }

    public Map<String, GroovyPlaylist> getPlaylists() {
        return GroovyBot.getInstance().getPlaylistManager().getPlaylist(entityId);
    }

    private void update() {
        GroovyBot.getInstance().getUserCache().update(this);
    }
}
