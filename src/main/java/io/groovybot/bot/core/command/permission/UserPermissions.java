package io.groovybot.bot.core.command.permission;

import io.groovybot.bot.core.entity.EntityProvider;
import io.groovybot.bot.core.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;

@Getter
@RequiredArgsConstructor
public class UserPermissions {

    private final User user;
    private final Boolean isOwner;
    private final Boolean isTierOne;
    private final Boolean isTierTwo;

    public boolean getAdminOnly(Guild guild) {
        return guild.getMemberById(user.getEntityId()).hasPermission(Permission.MANAGE_SERVER);
    }

    public boolean isDj(Guild guild) {
        if (!EntityProvider.getGuild(guild.getIdLong()).isDjMode())
            return true;
        if (guild.getMemberById(user.getEntityId()).getVoiceState().inVoiceChannel())
            if (guild.getMemberById(user.getEntityId()).getVoiceState().getChannel().getMembers().size() == 2)
                return true;
        System.out.println("dont work");
        for (Role role : guild.getMemberById(user.getEntityId()).getRoles()) {
            if (role.getName().toLowerCase().contains("dj"))
                return true;
        }
        return false;
    }
}
