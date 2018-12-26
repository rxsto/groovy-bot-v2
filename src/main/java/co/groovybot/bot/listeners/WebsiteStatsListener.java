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
import co.groovybot.bot.core.audio.LavalinkManager;
import co.groovybot.bot.io.WebsocketConnection;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@SuppressWarnings("unused")
@Log4j2
public class WebsiteStatsListener {

    @SubscribeEvent
    private void onGuildJoin(GuildJoinEvent event) {
        updateStats();
    }

    @SubscribeEvent
    private void onGuildLeave(GuildLeaveEvent event) {
        updateStats();
    }

    @SubscribeEvent
    private void onMemberJoin(GuildMemberJoinEvent event) {
        updateStats();
    }

    @SubscribeEvent
    private void onMemberLeave(GuildMemberLeaveEvent event) {
        updateStats();
    }

    private void updateStats() {
        if (GroovyBot.getInstance().getWebSocket() == null || GroovyBot.getInstance().getWebSocket().isClosed() || !GroovyBot.getInstance().getWebSocket().isOpen())
            return;
        GroovyBot.getInstance().getWebSocket().send(WebsocketConnection.parseMessage("bot", "poststats", WebsocketConnection.parseStats(LavalinkManager.countPlayers(), GroovyBot.getInstance().getShardManager().getGuilds().size(), GroovyBot.getInstance().getShardManager().getUsers().size())).toString());
    }
}
