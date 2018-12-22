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

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.entity.Guild;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AutoJoinExecutor {

    private final GroovyBot bot;

    @SubscribeEvent
    private void handleVoiceChannelLeave(GuildVoiceLeaveEvent event) {
        Guild guild = bot.getGuildCache().get(event.getGuild());
        VoiceChannel channel = event.getChannelLeft();
        if (!event.getMember().getUser().isBot() && guild.hasAutoJoinChannel() && channel.getIdLong() == guild.getAutoJoinChannelId() && guild.getAutoJoinChannel().getMembers().contains(event.getGuild().getSelfMember()) && channel.getMembers().size() == 1)
            bot.getMusicPlayerManager().getExistingPlayer(event.getGuild()).leave("No membery anymore in AutoJoin:tm: channel!");
    }

    @SubscribeEvent
    private void handleVoiceMove(GuildVoiceMoveEvent event) {
        Guild guild = bot.getGuildCache().get(event.getGuild());
        long autoChannelId = guild.getAutoJoinChannelId();
        if (event.getChannelJoined().getIdLong() == autoChannelId)
            handleVoiceChannelJoin(new GuildVoiceJoinEvent(event.getJDA(), event.getResponseNumber(), event.getMember()));
        else if (event.getChannelLeft().getIdLong() == autoChannelId)
            handleVoiceChannelLeave(new GuildVoiceLeaveEvent(event.getJDA(), event.getResponseNumber(), event.getMember(), event.getChannelLeft()));
    }

    @SubscribeEvent
    private void handleVoiceChannelJoin(GuildVoiceJoinEvent event) {
        Guild guild = bot.getGuildCache().get(event.getGuild());
        VoiceChannel channel = event.getChannelJoined();
        if (!event.getMember().getUser().isBot() && guild.hasAutoJoinChannel() && channel.getIdLong() == guild.getAutoJoinChannelId() && !guild.getAutoJoinChannel().getMembers().contains(event.getGuild().getSelfMember()) && !(event.getChannelJoined().getMembers().stream().filter(m -> !m.getUser().isBot()).collect(Collectors.toList()).size() > 1))
            bot.getMusicPlayerManager().getPlayer(event.getGuild(), null).connect(channel);
    }

    private <T extends GenericGuildVoiceEvent> void doChecks(T event, BiConsumer<T, Guild> callback) {
        System.out.println(1);
        Guild guild = bot.getGuildCache().get(event.getGuild());
        System.out.println(2);
        VoiceChannel channel = event instanceof GuildVoiceLeaveEvent ? ((GuildVoiceLeaveEvent) event).getChannelLeft() : event.getVoiceState().getChannel();
        System.out.println(channel);
        if (!event.getMember().getUser().isBot() && guild.hasAutoJoinChannel() && channel != null && channel.getIdLong() == guild.getAutoJoinChannelId())
            callback.accept(event, guild);
    }
}
