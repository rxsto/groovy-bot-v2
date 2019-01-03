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

package co.groovybot.bot.core.monitoring.monitors;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.LavalinkManager;
import co.groovybot.bot.core.monitoring.ActionMonitor;
import io.prometheus.client.Gauge;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class GuildMonitor implements ActionMonitor {

    private final Gauge guildCount;
    private final Gauge playingCount;

    public GuildMonitor() {
        this.guildCount = Gauge.build().namespace("groovy").name("guild_count").help("Show's the guild count.").register();
        this.playingCount = Gauge.build().namespace("groovy").name("playing_guilds").help("Show's the count of guilds, that groovy is playing on.").register();
        guildCount.set(GroovyBot.getInstance().getShardManager().getGuildCache().size());
    }

    @SubscribeEvent
    public void onGuildJoin(GuildJoinEvent event) {
        guildCount.set(GroovyBot.getInstance().getShardManager().getGuildCache().size());
    }

    @SubscribeEvent
    public void onGuildLeave(GuildLeaveEvent event) {
        guildCount.set(GroovyBot.getInstance().getShardManager().getGuildCache().size());
    }

    @Override
    public void action() {
        playingCount.set(LavalinkManager.countPlayers());
    }
}
