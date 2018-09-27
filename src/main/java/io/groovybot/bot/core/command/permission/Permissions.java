package io.groovybot.bot.core.command.permission;

import io.groovybot.bot.core.command.CommandEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Permissions {

    private final Boolean publicCommand;
    private final Boolean ownerOnly;
    private final Boolean tierOneOnly;
    private final Boolean tierTwoOnly;
    private final Boolean adminOnly;
    private final Boolean djOnly;

    public Boolean isCovered(UserPermissions permissions, CommandEvent event) {
        if (publicCommand)
            return true;
        if (permissions.getIsOwner())
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

    public static Permissions everyone() {
        return new Permissions(true, false, false, false, false, false);
    }

    public static Permissions ownerOnly() {
        return new Permissions(false, true, false, false, false, false);
    }

    public static Permissions tierOne() {
        return new Permissions(false, false, true, false, false, false);
    }

    public static Permissions tierTwo() {
        return new Permissions(false, false, false, true, false, false);
    }

    public static Permissions adminOnly() {
        return new Permissions(false, false,false, false, true, false);
    }

    public static Permissions djMode() {
        return new Permissions(false, false, false, false, false, true);
    }
}
