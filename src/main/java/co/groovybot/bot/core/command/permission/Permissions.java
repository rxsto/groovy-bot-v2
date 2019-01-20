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
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.premium.Tier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class Permissions {

    private final Boolean everyone;
    private final Boolean owner;
    private final Boolean tierone;
    private final Boolean tiertwo;
    private final Boolean tierthree;
    private final Boolean admin;
    private final Boolean dj;
    private final Boolean voted;
    @Getter
    private final String identifier;

    /**
     * Everyone can execute the command
     *
     * @return a Permission object
     */
    public static Permissions everyone() {
        return new Permissions(true, false, false, false, false, false, false, false, "Everyone");
    }

    /**
     * Only bot owners can execute the command
     *
     * @return a Permission object
     */
    public static Permissions ownerOnly() {
        return new Permissions(false, true, false, false, false, false, false, false, "Owner");
    }

    /**
     * Only tierOne or tierTwo patreons can execute the command
     *
     * @return a Permission object
     */
    public static Permissions tierOne() {
        return new Permissions(false, false, true, false, false, false, false, false, "Tier 1");
    }

    /**
     * Only tierTwo patreons can execute the command
     *
     * @return a Permission object
     */
    public static Permissions tierTwo() {
        return new Permissions(false, false, false, true, false, false, false, false, "Tier 2");
    }

    /**
     * Only tierThree patreons can execute the command
     *
     * @return a Permission object
     */
    public static Permissions tierThree() {
        return new Permissions(false, false, false, false, true, false, false, false, "Tier 3");
    }

    /**
     * Only users with the ADMINISTRATOR permission can execute the command
     *
     * @return a Permission object
     */
    public static Permissions adminOnly() {
        return new Permissions(false, false, false, false, false, true, false, false, "Admin");
    }

    /**
     * Only DJs can execute the command
     *
     * @return a Permission object
     */
    public static Permissions djMode() {
        return new Permissions(false, false, false, false, false, false, true, false, "DJ");
    }

    /**
     * Only users that voted for our bot on DBL
     *
     * @return a Permission object
     */
    public static Permissions votedOnly() {
        return new Permissions(false, false, false, false, false, false, false, true, "Voted");
    }

    public Boolean isCovered(UserPermissions permissions, CommandEvent event) {
        if (permissions.getIsOwner())
            return true;
        if (everyone)
            return true;
        if (owner)
            return permissions.getIsOwner();
        if (admin)
            return permissions.isAdmin(event.getGuild()) || event.getMember().hasPermission(Permission.MANAGE_SERVER);
        if (voted)
            return permissions.hasVoted();
        if (tierone)
            return permissions.isTierOne() || isPremiumGuild(event.getGuild());
        if (tiertwo)
            return permissions.isTierTwo() || isPremiumGuild(event.getGuild());
        if (tierthree)
            return permissions.isTierThree();
        if (dj)
            return permissions.isDj(event.getGuild());
        return false;
    }

    private boolean isPremiumGuild(Guild guild) {
        try (Connection connection = GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection()) {
            PreparedStatement tierThree = connection.prepareStatement("SELECT type FROM premium WHERE user_id = ?");
            tierThree.setLong(1, guild.getOwnerIdLong());
            ResultSet tierThreeSet = tierThree.executeQuery();
            if (tierThreeSet.next())
                if (Tier.valueOf(tierThreeSet.getString("type")) == Tier.THREE)
                    return true;
        } catch (SQLException e) {
            log.error("[PermissionProvider] Error while retrieving permissions!", e);
        }

        return false;
    }
}
