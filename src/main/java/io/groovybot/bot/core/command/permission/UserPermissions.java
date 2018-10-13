package io.groovybot.bot.core.command.permission;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.entity.EntityProvider;
import io.groovybot.bot.core.entity.User;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;

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

    public boolean getAdminOnly(Guild guild) {
        return guild.getMemberById(user.getEntityId()).hasPermission(Permission.MANAGE_SERVER);
    }

    public boolean isTierOne() {
        return retrievePatreonTier() >= 1;
    }

    public boolean isTierTwo() {
        return retrievePatreonTier() == 2;
    }

    private int retrievePatreonTier() {
        try {
            PreparedStatement ps = GroovyBot.getInstance().getPostgreSQL().getConnection().prepareStatement("SELECT type FROM premium WHERE user_id = ?");
            ps.setLong(1, user.getEntityId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("type");
            }
        } catch (SQLException e) {
            log.error("[PermissionProvider] Error while retrieving permissions!", e);
        }
        return 0;
    }

    public boolean isDj(Guild guild) {
        if (!EntityProvider.getGuild(guild.getIdLong()).isDjMode())
            return true;
        if (guild.getMemberById(user.getEntityId()).getVoiceState().inVoiceChannel())
            if (guild.getMemberById(user.getEntityId()).getVoiceState().getChannel().getMembers().size() == 2)
                return true;
        for (Role role : guild.getMemberById(user.getEntityId()).getRoles()) {
            if (role.getName().toLowerCase().contains("dj"))
                return true;
        }
        return false;
    }
}
