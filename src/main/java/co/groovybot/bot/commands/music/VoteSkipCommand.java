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

public class VoteSkipCommand extends SameChannelCommand {
    public VoteSkipCommand() {
        super(new String[] {"voteskip", "vs", "vskp"}, CommandCategory.MUSIC, Permissions.everyone(), "Vote for skipping the current song", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        MusicPlayer.VoteSkipReason reason = player.voteSkipAvailable();
        if (reason != MusicPlayer.VoteSkipReason.ALLOWED)
            return send(error(event.translate(reason.getTitleTranslationKey()), event.translate(reason.getDescriptionTranslationKey())));
        if (!player.incrementSkipVotes()) {
            return send(success(event.translate("command.voteskip.voted.title"), String.format(event.translate("command.voteskip.voted.description"), player.getSkipVotes(), player.getNeededSkipVotes())));
        }
        player.skip();
        return send(success(event.translate("command.voteskip.success.title"), event.translate("command.voteskip.success.description")));
    }
}
