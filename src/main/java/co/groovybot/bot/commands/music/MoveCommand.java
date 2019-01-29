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
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.utils.Helpers;

import java.util.LinkedList;

public class MoveCommand extends SameChannelCommand {

    public MoveCommand() {
        super(new String[]{"move", "mv"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you move a song from one position to another", "<song> <position>");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));

        if (args.length != 2 || !Helpers.isNumeric(args[0]) || !Helpers.isNumeric(args[1]))
            return sendHelp();

        int songPos = Integer.parseInt(args[0]);
        int wantPos = Integer.parseInt(args[1]);

        if (songPos > player.getTrackQueue().size() || wantPos > player.getTrackQueue().size() || songPos < 1 || wantPos < 1)
            return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalidnumbers.description")));

        if (songPos == wantPos)
            return send(error(event.translate("phrases.error"), event.translate("phrases.error.samenumbers")));

        LinkedList<AudioTrack> trackQueue = (LinkedList<AudioTrack>) player.getTrackQueue();

        int songPosIndex = songPos - 1;
        int wantPosIndex = wantPos - 1;

        AudioTrack preSave = trackQueue.get(songPosIndex);
        trackQueue.remove(songPosIndex);
        trackQueue.add(wantPosIndex, preSave);

        return send(success(event.translate("phrases.success"), String.format(event.translate("command.move"), preSave.getInfo().title, wantPos)));
    }
}
