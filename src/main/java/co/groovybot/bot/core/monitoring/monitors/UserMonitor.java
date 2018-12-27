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
import io.prometheus.client.Gauge;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class UserMonitor {

    private final Gauge member = Gauge.build().help("Show's member count").namespace("groovy").name("member_count").labelNames("guild").register();

    public UserMonitor() {
        GroovyBot.getInstance().getShardManager().getGuildCache().forEach(g -> {
            member.labels(g.getId()).set(g.getMemberCache().size());
        });
    }

    @SubscribeEvent
    public void onMemberJoin(GuildMemberJoinEvent event) {
        member.labels(event.getGuild().getId()).set(event.getGuild().getMemberCache().size());
    }

    @SubscribeEvent
    public void onMemberLeave(GuildMemberLeaveEvent event) {
        member.labels(event.getGuild().getId()).set(event.getGuild().getMemberCache().size());
    }
}
