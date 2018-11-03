package io.groovybot.bot.listeners;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.events.bot.AllShardsLoadedEvent;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@Log4j2
public class ShardsListener {

    private int tempLoadedShards = 0;

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onReady(ReadyEvent event) {
        ShardManager shardManager = GroovyBot.getInstance().getShardManager();

        tempLoadedShards++;

        log.info(String.format("[ShardManager] Successfully launched Shard %s! (%s/%s)", event.getJDA().getShardInfo().getShardId() + 1, tempLoadedShards, shardManager.getShardsTotal()));

        if (tempLoadedShards == shardManager.getShardsTotal()) {
            log.info(String.format("[ShardManager] Successfully launched %s %s! Finishing Startup ...", tempLoadedShards, tempLoadedShards == 1 ? "Shards" : "Shard"));

            GroovyBot.getInstance().getEventManager().handle(new AllShardsLoadedEvent(event.getJDA(), event.getResponseNumber()));
        }
    }
}
