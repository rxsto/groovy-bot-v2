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
import co.groovybot.bot.core.command.permission.UserPermissions;
import co.groovybot.bot.util.EmbedUtil;
import co.groovybot.bot.util.SafeMessage;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class PremiumExecutor {

    private final GroovyBot bot;

    @SubscribeEvent
    private void handleJoin(GuildJoinEvent event) {
        for (Member member : event.getGuild().getMembers().stream().filter(member -> member.hasPermission(Permission.ADMINISTRATOR) || member.isOwner()).collect(Collectors.toList()))
            if (new UserPermissions(bot.getUserCache().get(member.getUser().getIdLong()), bot).isAbleToInvite()) return;

        event.getGuild().getTextChannels().stream().filter(TextChannel::canTalk).findFirst().ifPresent(channel -> SafeMessage.sendMessage(channel, EmbedUtil.small(String.format("%s left this server as there is no member with administrative permissions that subscribed to any tier. In order to be able to use Groovy Premium Bot you need to donate [here](https://donate.groovybot.co).", event.getJDA().getSelfUser().getName()))));
        event.getGuild().leave().queue();
    }
}
