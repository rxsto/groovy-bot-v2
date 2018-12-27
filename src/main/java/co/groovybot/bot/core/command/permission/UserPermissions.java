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

package co.groovybot.bot.core.command.permission;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.core.entity.User;
import co.groovybot.bot.core.premium.Tier;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Log4j2
public class UserPermissions {

    private final User user;
    private final Boolean isOwner;

    public UserPermissions(User user, GroovyBot bot) {
        this.user = user;
        this.isOwner = bot.getConfig().getJSONArray("owners").toString().contains(user.getEntityId().toString());
    }

    public boolean isAdmin(Guild guild) {
        return guild.getMemberById(user.getEntityId()).hasPermission(Permission.MANAGE_SERVER, Permission.ADMINISTRATOR);
    }

    public boolean isTierOne() {
        Tier tier = retrievePatreonTier();
        return tier == Tier.ONE || tier == Tier.TWO || tier == Tier.THREE;
    }

    public boolean isTierTwo() {
        Tier tier = retrievePatreonTier();
        return tier == Tier.TWO || tier == Tier.THREE;
    }

    public boolean isTierThree() {
        return retrievePatreonTier() == Tier.THREE;
    }

    private Tier retrievePatreonTier() {
        if (isOwner) return Tier.THREE;
        try (Connection connection = GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection()) {
            PreparedStatement premium = connection.prepareStatement("SELECT type FROM premium WHERE user_id = ?");
            premium.setLong(1, user.getEntityId());
            ResultSet premiumSet = premium.executeQuery();
            if (premiumSet.next())
                if (Tier.valueOf(premiumSet.getString("type")) != Tier.NONE)
                    return Tier.valueOf(premiumSet.getString("type"));

            PreparedStatement friend = connection.prepareStatement("SELECT friend FROM users WHERE user_id = ?");
            friend.setLong(1, user.getEntityId());
            ResultSet friendSet = friend.executeQuery();
            if (friendSet.next())
                if (friendSet.getBoolean("friend"))
                    return Tier.TWO;

            PreparedStatement voted = connection.prepareStatement("SELECT expiration FROM users WHERE user_id = ?");
            voted.setLong(1, user.getEntityId());
            ResultSet votedSet = voted.executeQuery();
            if (votedSet.next())
                if (votedSet.getLong("expiration") > System.currentTimeMillis())
                    return Tier.TWO;
        } catch (SQLException e) {
            log.error("[PermissionProvider] Error while retrieving permissions!", e);
        }

        return Tier.NONE;
    }

    public boolean isAbleToInvite() {
        if (isOwner) return true;
        try (Connection connection = GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection()) {
            PreparedStatement premium = connection.prepareStatement("SELECT type FROM premium WHERE user_id = ?");
            premium.setLong(1, user.getEntityId());
            ResultSet premiumSet = premium.executeQuery();
            if (premiumSet.next())
                return Tier.valueOf(premiumSet.getString("type")) != Tier.NONE;

            PreparedStatement friend = connection.prepareStatement("SELECT friend FROM users WHERE user_id = ?");
            friend.setLong(1, user.getEntityId());
            ResultSet friendSet = friend.executeQuery();
            if (friendSet.next())
                return friendSet.getBoolean("friend");
        } catch (SQLException e) {
            log.error("[PermissionProvider] Error while retrieving permissions!", e);
        }

        return false;
    }

    public boolean isDj(Guild guild) {
        if (isOwner) return true;
        co.groovybot.bot.core.entity.Guild gguild = EntityProvider.getGuild(guild.getIdLong());
        if (!gguild.isDjMode())
            return true;
        if (guild.getMemberById(user.getEntityId()).getVoiceState().inVoiceChannel())
            if (guild.getMemberById(user.getEntityId()).getVoiceState().getChannel().getMembers().size() == 2)
                return true;
        for (Role role : guild.getMemberById(user.getEntityId()).getRoles()) {
            log.debug(role.getIdLong());
            log.debug(gguild.getDjRole());
            if (role.getIdLong() == gguild.getDjRole())
                return true;
        }
        return false;
    }

    public boolean hasVoted() {
        Request request = new Request.Builder()
                .url(new HttpUrl.Builder()
                        .scheme("https")
                        .host("discordbots.org")
                        .addPathSegments(String.format("api/bots/%s/check", "402116404301660181"))
                        .addQueryParameter("userId", String.valueOf(user.getEntityId()))
                        .build())
                .addHeader("Authorization", GroovyBot.getInstance().getConfig().getJSONObject("botlists").getString("discordbots.org"))
                .get()
                .build();
        try (Response response = GroovyBot.getInstance().getHttpClient().newCall(request).execute()) {
            assert response.body() != null;
            return new JSONObject(response.body().string()).getInt("voted") == 1;
        } catch (IOException e) {
            log.error("[DBL] Error occurred while retrieving vote information");
            return false;
        }
    }
}
