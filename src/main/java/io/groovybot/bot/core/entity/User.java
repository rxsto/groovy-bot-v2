package io.groovybot.bot.core.entity;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.command.permission.UserPermissions;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;

@Getter
public class User extends DatabaseEntitiy {

    private UserPermissions permissions;
    private Locale locale = GroovyBot.getInstance().getTranslationManager().getDefaultLocale().getLocale();

    public User(Long entityId) throws Exception {
        super(entityId);
        boolean tierOne = false;
        boolean tierTwo = false;
        Boolean owner = GroovyBot.getInstance().getConfig().getJSONArray("owners").toString().contains(String.valueOf(entityId));
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM premium WHERE user_id = ?");
        ps.setLong(1, entityId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            tierOne = rs.getString("type").equals("1");
            tierTwo = rs.getString("type").equals("2");
            locale = Locale.forLanguageTag(rs.getString("locale").replace("_", "-"));
        } else {
            PreparedStatement insertStatement = getConnection().prepareStatement("INSERT INTO users (id, locale) VALUES (?, ?)");
            insertStatement.setLong(1, entityId);
            insertStatement.setString(2, locale.toLanguageTag().replace("-", "_"));
        }
        permissions = new UserPermissions(this, owner, tierOne, tierTwo);
    }

    @Override
    public void updateInDatabase() throws Exception {
        PreparedStatement ps = getConnection().prepareStatement("UPDATE users SET locale = ? WHERE id = ?");
        ps.setString(1, locale.toLanguageTag().replace("-", "_"));
        ps.setLong(2, entityId);
        ps.execute();
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        update();
    }

    private void update() {
        GroovyBot.getInstance().getUserCache().update(this);
    }
}
