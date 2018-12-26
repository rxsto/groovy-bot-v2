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
import co.groovybot.bot.core.cache.Cache;
import co.groovybot.bot.core.entity.Guild;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class BlacklistWatcher {

    private final Cache<Guild> guildCache;

    @SubscribeEvent
    private void onTextChannelDelete(TextChannelDeleteEvent event) {
        final Guild guild = guildCache.get(event.getGuild().getIdLong());
        final long channelId = event.getChannel().getIdLong();
        if (guild.isChannelBlacklisted(channelId))
            guild.unBlacklistChannel(channelId);
        if (event.getChannel().getIdLong() == guild.getBotChannel())
            guild.setBotChannel(0);
        guild.getBlacklistedChannels().forEach(channel -> {
            if (GroovyBot.getInstance().getShardManager().getTextChannelById((long) channel) == null)
                guild.unBlacklistChannel((long) channel);
        });
    }
}
