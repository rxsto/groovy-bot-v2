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

package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SemiInChannelCommand;
import co.groovybot.bot.util.FormatUtil;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PlayCommand extends SemiInChannelCommand {

    public PlayCommand() {
        super(new String[]{"play", "p", "add"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets you play any music you want", FormatUtil.formatHelp("play [options] <url/search>", MusicPlayer.CLI_OPTIONS));
    }

    @Override
    public Result executeCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0) {
            if (player.isPaused()) {
                player.resume();
                return send(success(event.translate("phrases.success"), event.translate("command.resume")));
            }

            return sendHelp();
        }

        player.queueSongs(event);

        return null;
    }
}
