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

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.time.Instant;

@SuppressWarnings("unused")
public class GuildLeaveListener {

    @SubscribeEvent
    private void handleGuildKick(GuildLeaveEvent event) {
        event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new EmbedBuilder().setTitle("Oh no!").setDescription("We are sorry that Groovy does not fit your expectations! If there is anything we can do better please let us know! Join our support-guild: https://support.groovybot.co").setFooter("Help us improving Groovy!", null).setTimestamp(Instant.now()).build()).queue(ignored -> {
        }, ignored2 -> {
        }));
    }
}
