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
import co.groovybot.bot.core.command.voice.SameChannelCommand;
import co.groovybot.bot.util.FormatUtil;
import net.dv8tion.jda.core.utils.Helpers;

public class JumpCommand extends SameChannelCommand {

    public JumpCommand() {
        super(new String[]{"jump", "jumpto", "jp"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you seek forwards or backwards", "[-]<seconds>");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0)
            return sendHelp();

        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));

        String input = args[0].replace("-", "");

        if (!Helpers.isNumeric(input))
            return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalid.number")));

        if (input.length() > ("" + player.getPlayer().getPlayingTrack().getDuration() / 1000).length()) {
            if (args[0].startsWith("-"))
                return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalid.number")));

            player.seekTo(player.getPlayer().getPlayingTrack().getDuration());
            return send(info(event.translate("phrases.skipped"), event.translate("command.jump.skipped.description")));
        }

        int seconds = Integer.parseInt(input) * 1000;

        if (args[0].startsWith("-") && seconds > player.getPlayer().getTrackPosition())
            return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalid.number")));

        if (args[0].startsWith("-"))
            seconds = ~seconds;

        long current = player.getPlayer().getTrackPosition();
        long position = current + seconds;

        player.seekTo(position);

        if (position > player.getPlayer().getPlayingTrack().getDuration())
            return send(info(event.translate("phrases.skipped"), event.translate("command.jump.skipped")));

        return send(success(event.translate("phrases.success"), String.format(event.translate("command.jump"), FormatUtil.formatTimestamp(current), FormatUtil.formatTimestamp(position))));
    }
}
