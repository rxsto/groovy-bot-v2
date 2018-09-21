package io.groovybot.bot.listeners;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.events.bot.AllShardsLoadedEvent;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class ShardsListener {

    private int tempLoadedShards = 0;


    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onReady(ReadyEvent event) {
        tempLoadedShards++;
        ShardManager shardManager = GroovyBot.getInstance().getShardManager();
        if (tempLoadedShards == shardManager.getShardsTotal())
            GroovyBot.getInstance().getEventManager().handle(new AllShardsLoadedEvent(event.getJDA(), 200));
    }
}
