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
import co.groovybot.bot.util.EmbedUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.utils.Helpers;

import java.util.LinkedList;

@Log4j2
public class RemoveCommand extends SameChannelCommand {

    public RemoveCommand() {
        super(new String[]{"remove", "rm"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you remove a specific song from the queue", "<position>");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));

        if (args.length == 0)
            return sendHelp();

        if (!Helpers.isNumeric(args[0]))
            return send(EmbedUtil.error(event.translate("phrases.invalid"), event.translate("phrases.invalid.number")));

        int query;

        try {
            query = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return send(EmbedUtil.error(event.translate("phrases.invalid"), event.translate("phrases.invalid.number")));
        }

        if (query > player.trackQueue.size() || query < 1)
            return send(EmbedUtil.error(event.translate("phrases.error"), event.translate("command.remove.notinqueue")));

        String title = ((LinkedList<AudioTrack>) player.trackQueue).get(query - 1).getInfo().title;
        ((LinkedList<AudioTrack>) player.trackQueue).remove(query - 1);

        return send(EmbedUtil.success(event.translate("phrases.success"), String.format(event.translate("command.remove"), title, query)));
    }
}
