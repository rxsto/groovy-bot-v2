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

package co.groovybot.bot.core.command.voice;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;

public abstract class SemiInChannelCommand extends SameChannelCommand {

    public SemiInChannelCommand(String[] aliases, CommandCategory commandCategory, Permissions permissions, String description, String usage) {
        super(aliases, commandCategory, permissions, description, usage);
    }

    @Override
    public Result execute(String[] args, CommandEvent event, MusicPlayer player) {
        if (event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
            return super.execute(args, event, player);
        if (player.checkConnect(event)) {
            player.connect(event.getMember().getVoiceState().getChannel());
            return executeCommand(args, event, player);
        }

        return null;
    }

    protected abstract Result executeCommand(String[] args, CommandEvent event, MusicPlayer player);

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        return executeCommand(args, event, player);
    }
}
