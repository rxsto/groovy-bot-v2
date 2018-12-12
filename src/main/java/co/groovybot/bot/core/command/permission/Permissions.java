package co.groovybot.bot.core.command.permission;

import co.groovybot.bot.core.command.CommandEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Permissions {

    private final Boolean everyone;
    private final Boolean owner;
    private final Boolean tierone;
    private final Boolean tiertwo;
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
        return new Permissions(true, false, false, false, false, false, false, "everyone");
    }

    /**
     * Only bot owners can execute the command
     *
     * @return a Permission object
     */
    public static Permissions ownerOnly() {
        return new Permissions(false, true, false, false, false, false, false, "owner");
    }

    /**
     * Only tierOne or tierTwo patreons can execute the command
     *
     * @return a Permission object
     */
    public static Permissions tierOne() {
        return new Permissions(false, false, true, false, false, false, false, "tierone");
    }

    /**
     * Only tierTwo patreons can execute the command
     *
     * @return a Permission object
     */
    public static Permissions tierTwo() {
        return new Permissions(false, false, false, true, false, false, false, "tiertwo");
    }

    /**
     * Only users with the ADMINISTRATOR permission can execute the command
     *
     * @return a Permission object
     */
    public static Permissions adminOnly() {
        return new Permissions(false, false, false, false, true, false, false, "admin");
    }

    /**
     * Only DJs can execute the command
     *
     * @return a Permission object
     */
    public static Permissions djMode() {
        return new Permissions(false, false, false, false, false, true, false, "djmode");
    }

    /**
     * Only users that voted for our bot on DBL
     *
     * @return a Permission object
     */
    public static Permissions votedOnly() {
        return new Permissions(false, false, false, false, false, false, true, "voted");
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
            return permissions.isTierOne();
        if (tiertwo)
            return permissions.isTierTwo();
        if (dj)
            return permissions.isDj(event.getGuild());
        return false;
    }
}
