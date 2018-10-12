package io.groovybot.bot.core.events;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.listeners.*;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;

public class EventRegistry {

    public EventRegistry(DefaultShardManagerBuilder builder, GroovyBot bot) {
        builder.addEventListeners(
                new ShardsListener(),
                new CommandLogger(),
                new GuildLogger(),
                new SelfMentionListener(),
                //new BetaListener(),
                bot.getCommandManager(),
                bot,
                bot.getLavalinkManager(),
                bot.getInteractionManager(),
                bot.getEventWaiter()
        );
    }
}
