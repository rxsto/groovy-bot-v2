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
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.SubCommand;
import co.groovybot.bot.core.command.permission.Permissions;

public abstract class SemiInChannelSubCommand extends SubCommand {

    public SemiInChannelSubCommand(String[] aliases, Permissions permissions, String description, String usage) {
        super(aliases, permissions, description, usage);
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        MusicPlayer player = event.getBot().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());
        if (event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
            if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
                return send(error(event.translate("phrases.notinchannel.title"), event.translate("phrases.notinchannel.description")));
            if (!event.getGuild().getSelfMember().getVoiceState().getChannel().equals(event.getMember().getVoiceState().getChannel()))
                return send(error(event.translate("phrases.notsamechannel.title"), event.translate("phrases.notsamechannel.description")));
            return executeCommand(args, event, player);
        }

        if (player.checkConnect(event)) {
            player.connect(event.getMember().getVoiceState().getChannel());
            return executeCommand(args, event, player);
        }
        return send(error(event));
    }

    protected abstract Result executeCommand(String[] args, CommandEvent event, MusicPlayer player);

}
