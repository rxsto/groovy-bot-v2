package io.groovybot.bot.core.entity;

import io.groovybot.bot.GroovyBot;
import lombok.Getter;

import java.sql.Connection;

public abstract class DatabaseEntitiy {

    @Getter
    public final Long entityId;

    protected DatabaseEntitiy(Long entityId) throws Exception{
        this.entityId = entityId;
    }

    public abstract void updateInDatabase() throws Exception;

    protected Connection getConnection() {
        return GroovyBot.getInstance().getPostgreSQL().getConnection();
    }


}
