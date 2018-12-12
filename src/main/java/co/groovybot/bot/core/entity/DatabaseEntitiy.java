package co.groovybot.bot.core.entity;

import co.groovybot.bot.GroovyBot;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseEntitiy {

    @Getter
    public final Long entityId;

    protected DatabaseEntitiy(Long entityId) {
        this.entityId = entityId;
    }

    public abstract void updateInDatabase() throws Exception;

    protected Connection getConnection() throws SQLException {
        return GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DatabaseEntitiy))
            return false;
        return ((DatabaseEntitiy) obj).getEntityId().equals(entityId);
    }
}
