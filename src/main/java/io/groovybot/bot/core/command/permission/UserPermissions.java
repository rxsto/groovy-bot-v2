package io.groovybot.bot.core.command.permission;

import io.groovybot.bot.core.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

@Getter
@RequiredArgsConstructor
public class UserPermissions {

    private final User user;
    private final Boolean isOwner;
    private final Boolean isTierOne;
    private final Boolean isTierTwo;

    public Boolean getAdminOwnly(Guild guild) {
        return guild.getMemberById(user.getEntityId()).hasPermission(Permission.MANAGE_SERVER);
    }
}
