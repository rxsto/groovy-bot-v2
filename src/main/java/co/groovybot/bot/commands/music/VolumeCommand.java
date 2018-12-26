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
        super(new String[]{"volume", "vol"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you set Groovy's volume", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        int volume;
        if (args.length == 0)
            return send(info(event.translate("command.volume.current.title"), String.format(event.translate("command.volume.current.description"), player.getPlayer().getVolume())));
        try {
            volume = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return send(error(event.translate("phrases.invalidnumber.title"), event.translate("phrases.invalidnumber.description")));

        }
        if (volume > 200 || volume < 0)
            return send(error(event.translate("command.volume.tohigh.title"), event.translate("command.volume.tohigh.description")));
        player.setVolume(volume);
        return send(success(event.translate("command.volume.set.title"), String.format(event.translate("command.volume.set.description"), volume)));
    }
}
