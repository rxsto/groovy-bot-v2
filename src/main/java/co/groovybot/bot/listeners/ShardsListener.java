/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

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
            log.info(String.format("[ShardingManager] Successfully launched %s %s!", shardManager.getShardsTotal(), shardManager.getShardsTotal() == 1 ? "Shard" : "Shards"));
            GroovyBot.getInstance().getEventManager().handle(new AllShardsLoadedEvent(event.getJDA(), event.getResponseNumber()));
        }
    }
}
