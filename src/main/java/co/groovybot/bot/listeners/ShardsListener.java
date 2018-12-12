package co.groovybot.bot.listeners;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.events.bot.AllShardsLoadedEvent;
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

        if (tempLoadedShards == shardManager.getShardsTotal()) {
            log.info(String.format("[ShardManager] Successfully launched %s %s! Finishing Startup ...", shardManager.getShardsTotal(), shardManager.getShardsTotal() == 1 ? "Shard" : "Shards"));
            GroovyBot.getInstance().getEventManager().handle(new AllShardsLoadedEvent(event.getJDA(), event.getResponseNumber()));
        }
    }
}
