package io.groovybot.bot.core.command.permission;

import io.groovybot.bot.core.command.CommandEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Permissions {

    private final Boolean publicCommand;
    private final Boolean ownerOnly;
    private final Boolean tierOneOnly;
    private final Boolean tierTwoOnly;
    private final Boolean adminOnly;
    private final Boolean djOnly;
    @Getter
    private final String identifier;

    /**
     * Everyone can execute the command
     * @return a Permission object
     */
    public static Permissions everyone() {
        return new Permissions(true, false, false, false, false, false, "everyone");
    }

    /**
     * Only bot owners can execute the command
     * @return a Permission object
     */
    public static Permissions ownerOnly() {
        return new Permissions(false, true, false, false, false, false, "owner");
    }

    /**
     * Only tierOne or tierTwo patreons can execute the command
     * @return a Permission object
     */
    public static Permissions tierOne() {
        return new Permissions(false, false, true, false, false, false, "tierone");
    }

    /**
     * Only tierTwo patreons can execute the command
     * @return a Permission object
     */
    public static Permissions tierTwo() {
        return new Permissions(false, false, false, true, false, false, "tiertwo");
    }

    /**
     * Only users with the ADMINISTRATOR permission can execute the command
     * @return a Permission object
     */
    public static Permissions adminOnly() {
        return new Permissions(false, false, false, false, true, false, "admin");
    }

    /**
     * Only DJs can execute the command
     * @return a Permission object
     */
    public static Permissions djMode() {
        return new Permissions(false, false, false, false, false, true, "djmode");
    }

    public Boolean isCovered(UserPermissions permissions, CommandEvent event) {
        if (permissions.getIsOwner())
            return true;
        if (publicCommand)
            return true;
        if (ownerOnly)
            return permissions.getIsOwner();
        if (adminOnly)
            return permissions.getAdminOnly(event.getGuild());
        if (tierOneOnly)
            return permissions.getIsTierOne() || permissions.getIsTierTwo();
        if (tierTwoOnly)
            return permissions.getIsTierTwo();
        if (djOnly)
            return permissions.isDj(event.getGuild());
        return false;
    }
}
