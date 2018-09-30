package io.groovybot.bot.core.command.permission;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.entity.User;
import lombok.extern.log4j.Log4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Log4j
public class PermissionProvider {

    public static UserPermissions getUserPermissions(User user) {
        boolean tierOne = false;
        boolean tierTwo = false;
        final Long entityId = user.getEntityId();
        try {
            PreparedStatement ps = GroovyBot.getInstance().getPostgreSQL().getConnection().prepareStatement("SELECT type FROM premium WHERE user_id = ?");
            ps.setLong(1, entityId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tierOne = rs.getString("type").equals("1");
                tierTwo = rs.getString("type").equals("2");
            }
        } catch (SQLException e) {
            log.error("[PermissionProvider] Error while retrieving permissions!", e);
        }
        return new UserPermissions(user, GroovyBot.getInstance().getConfig().getJSONArray("owners").toString().contains(String.valueOf(entityId)), tierOne, tierTwo);
    }
}
