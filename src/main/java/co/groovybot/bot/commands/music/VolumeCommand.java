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

public class VolumeCommand extends SameChannelCommand {
    public VolumeCommand() {
        super(new String[]{"volume", "vol", "v"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you set Groovy's volume", "<volume>");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));

        if (args.length == 0)
            return send(small(String.format("%s%s", player.getPlayer().getVolume(), "%")));

        int volume;

        try {
            volume = Integer.parseInt(args[0].replace("%", ""));
        } catch (NumberFormatException e) {
            return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalid.number")));

        }

        if (volume > 200 || volume < 0)
            return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalid.volume")));

        int current = player.getPlayer().getVolume();

        player.setVolume(volume);
        return send(success(event.translate("phrases.success"), String.format(event.translate("command.volume"), current, volume)));
    }
}
