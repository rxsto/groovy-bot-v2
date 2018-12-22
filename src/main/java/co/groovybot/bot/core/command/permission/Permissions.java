package co.groovybot.bot.core.command.permission;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.premium.Tier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

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
        return new Permissions(true, false, false, false, false, false, false, false, "everyone");
    }

    /**
     * Only bot owners can execute the command
     *
     * @return a Permission object
     */
    public static Permissions ownerOnly() {
        return new Permissions(false, true, false, false,false, false, false, false, "owner");
    }

    /**
     * Only tierOne or tierTwo patreons can execute the command
     *
     * @return a Permission object
     */
    public static Permissions tierOne() {
        return new Permissions(false, false, true, false, false, false, false, false, "tierone");
    }

    /**
     * Only tierTwo patreons can execute the command
     *
     * @return a Permission object
     */
    public static Permissions tierTwo() {
        return new Permissions(false, false, false, true, false, false, false, false, "tiertwo");
    }

    /**
     * Only tierThree patreons can execute the command
     *
     * @return a Permission object
     */
    public static Permissions tierThree() {
        return new Permissions(false, false, false, false, true, false, false, false, "tierthree");
    }

    /**
     * Only users with the ADMINISTRATOR permission can execute the command
     *
     * @return a Permission object
     */
    public static Permissions adminOnly() {
        return new Permissions(false, false, false, false, false, true, false, false, "admin");
    }

    /**
     * Only DJs can execute the command
     *
     * @return a Permission object
     */
    public static Permissions djMode() {
        return new Permissions(false, false, false, false, false, false, true, false, "djmode");
    }

    /**
     * Only users that voted for our bot on DBL
     *
     * @return a Permission object
     */
    public static Permissions votedOnly() {
        return new Permissions(false, false, false, false, false, false, false, true, "voted");
    }

    public Boolean isCovered(UserPermissions permissions, CommandEvent event) {
        if (permissions.getIsOwner())
            return true;
        if (everyone)
            return true;
        if (owner)
            return permissions.getIsOwner();
        if (admin)
            return permissions.isAdmin(event.getGuild());
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
