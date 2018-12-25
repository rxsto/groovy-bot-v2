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
        super(new String[]{"jump", "jumpto"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you seek forwards or backwards", "[-]<seconds>");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0)
            return sendHelp();
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        String input = args[0].replace("-", "");
        if (!Helpers.isNumeric(input)) {
            return send(error(event.translate("phrases.invalidnumber.title"), event.translate("phrases.invalidnumber.description")));
        }
        if (input.length() > ("" + player.getPlayer().getPlayingTrack().getDuration() / 1000).length()) {
            player.seekTo(player.getPlayer().getPlayingTrack().getDuration());
            return send(error(event.translate("command.jumpto.skipped.title"), event.translate("command.jumpto.skipped.description")));
        }
        int seconds = Integer.parseInt(input) * 1000;
        if (args[0].startsWith("-"))
            seconds = ~seconds;
        long position = player.getPlayer().getTrackPosition() + seconds;
        player.seekTo(position);
        if (position > player.getPlayer().getPlayingTrack().getDuration())
            return send(error(event.translate("command.jumpto.skipped.title"), event.translate("command.jumpto.skipped.description")));
        return send(success(event.translate("command.jumpto.success.title"), String.format(event.translate("command.jumpto.success.description"), FormatUtil.formatTimestamp(position))));
    }
}
