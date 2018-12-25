/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergeij Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
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

import co.groovybot.bot.util.EmbedUtil;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.*;

@Log4j2
public class JoinGuildListener {

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onGuildJoin(GuildJoinEvent event) {
        joinMessage(event.getGuild());
    }

    private void joinMessage(Guild guild) {
        List<TextChannel> channels = guild.getTextChannels();

        Map<String, TextChannel> sortedChannels = new HashMap<>();
        Set<TextChannel> preferredChannels = new HashSet<>();

        channels.forEach(channel -> sortedChannels.put(channel.getName(), channel));

        sortedChannels.forEach((name, channel) -> {
            if (name.contains("music") || name.contains("bot") || name.contains("command") || name.contains("talk") || name.contains("chat") || name.contains("general"))
                preferredChannels.add(channel);
        });

        boolean found = false;

        for (TextChannel channel : preferredChannels)
            if (channel.canTalk()) {
                EmbedUtil.sendMessageBlocking(channel, EmbedUtil.welcome(guild));
                found = true;
                break;
            }

        if (!found)
            for (TextChannel channel : channels)
                if (channel.canTalk()) {
                    EmbedUtil.sendMessageBlocking(channel, EmbedUtil.welcome(guild));
                    break;
                }
    }
}
