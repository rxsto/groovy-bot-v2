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
import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.core.entity.entities.GroovyGuild;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@Log4j2
public class AutoPauseListener {

    @SubscribeEvent
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if (event.getChannelLeft().getMembers().stream().anyMatch(m -> m.getUser().getIdLong() == event.getGuild().getSelfMember().getUser().getIdLong()) && event.getChannelLeft().getMembers().size() == 1)
            handleAutopauseStart(event);
        if (event.getChannelJoined().getMembers().stream().anyMatch(m -> m.getUser().getIdLong() == event.getGuild().getSelfMember().getUser().getIdLong()) && event.getChannelJoined().getMembers().size() > 1)
            handleAutopauseStop(event);
    }

    @SubscribeEvent
    private void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (event.getChannelLeft().getMembers().stream().anyMatch(m -> m.getUser().getIdLong() == event.getGuild().getSelfMember().getUser().getIdLong()) && event.getChannelLeft().getMembers().size() == 1)
            handleAutopauseStart(event);
    }

    @SubscribeEvent
    private void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (event.getChannelJoined().getMembers().stream().anyMatch(m -> m.getUser().getIdLong() == event.getGuild().getSelfMember().getUser().getIdLong()) && event.getChannelJoined().getMembers().size() > 1)
            handleAutopauseStop(event);
    }

    private void handleAutopauseStart(GenericGuildVoiceEvent event) {
        GroovyGuild groovyGuild = EntityProvider.getGuild(event.getGuild().getIdLong());
        if (!groovyGuild.isAutoPause())
            return;
        MusicPlayer musicPlayer = GroovyBot.getInstance().getMusicPlayerManager().getExistingPlayer(event.getGuild());
        if (musicPlayer == null)
            return;
        musicPlayer.getPlayer().setPaused(true);
        musicPlayer.getHandler().handleTrackPause();
    }

    private void handleAutopauseStop(GenericGuildVoiceEvent event) {
        GroovyGuild groovyGuild = EntityProvider.getGuild(event.getGuild().getIdLong());
        if (!groovyGuild.isAutoPause())
            return;
        MusicPlayer musicPlayer = GroovyBot.getInstance().getMusicPlayerManager().getExistingPlayer(event.getGuild());
        if (musicPlayer == null)
            return;
        musicPlayer.getPlayer().setPaused(false);
        musicPlayer.getHandler().handleTrackResume();
    }
}
