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

    public Boolean isCovered(UserPermissions permissions, CommandEvent event) {
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

    public static Permissions everyone() {
        return new Permissions(true, false, false, false, false, false, "everyone");
    }

    public static Permissions ownerOnly() {
        return new Permissions(false, true, false, false, false, false, "owner");
    }

    public static Permissions tierOne() {
        return new Permissions(false, false, true, false, false, false, "tierone");
    }

    public static Permissions tierTwo() {
        return new Permissions(false, false, false, true, false, false, "tiertwo");
    }

    public static Permissions adminOnly() {
        return new Permissions(false, false,false, false, true, false, "admin");
    }

    public static Permissions djMode() {
        return new Permissions(false, false, false, false, false, true, "djmode");
    }
}
