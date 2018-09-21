package io.groovybot.bot.core.entity;

import io.groovybot.bot.GroovyBot;

public class EntityProvider {

    public static User getUser(Long entityId) {
        return GroovyBot.getInstance().getUserCache().get(entityId);
    }

    public static Guild getGuild(Long entityId) {
        return GroovyBot.getInstance().getGuildCache().get(entityId);
    }
}

