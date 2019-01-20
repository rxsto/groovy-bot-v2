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
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.util.EmbedUtil;
import co.groovybot.bot.util.SafeMessage;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class SelfMentionListener {

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onMention(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals(event.getGuild().getSelfMember().getAsMention())) {
            String prefix = EntityProvider.getGuild(event.getGuild().getIdLong()) == null ? EntityProvider.getGuild(event.getGuild().getIdLong()).getPrefix() : GroovyBot.getInstance().getConfig().getJSONObject("settings").getString("prefix");
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.small(String.format("<a:hey:526016403694813195> **Hey!** My **prefix** is **`%s`**, you'll get **my commands** with **`%shelp`**", prefix, prefix)));
        }
    }
}
