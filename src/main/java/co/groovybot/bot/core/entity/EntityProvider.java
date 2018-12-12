package co.groovybot.bot.core.entity;

import co.groovybot.bot.GroovyBot;

public class EntityProvider {

    public static User getUser(Long entityId) {
        return GroovyBot.getInstance().getUserCache().get(entityId);
    }

    public static Guild getGuild(Long entityId) {
        return GroovyBot.getInstance().getGuildCache().get(entityId);
    }
}

