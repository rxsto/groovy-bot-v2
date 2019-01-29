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

import co.groovybot.bot.util.EmbedUtil;
import co.groovybot.bot.util.SafeMessage;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@Log4j2
public class GuildJoinListener {

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onGuildJoin(GuildJoinEvent event) {
        joinMessage(event.getGuild());
    }

    private void joinMessage(Guild guild) {
        TextChannel textChannel = guild.getTextChannels().stream().filter(TextChannel::canTalk).filter(channel -> channel.getName().toLowerCase().contains("bot") || channel.getName().toLowerCase().contains("command") || channel.getName().toLowerCase().contains("music")).findFirst().orElse(guild.getTextChannels().stream().filter(TextChannel::canTalk).findFirst().orElse(null));
        if (textChannel != null)
            SafeMessage.sendMessage(textChannel, EmbedUtil.welcome(guild));
    }
}
