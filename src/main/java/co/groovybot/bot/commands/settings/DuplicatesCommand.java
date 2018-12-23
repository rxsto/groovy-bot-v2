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

package co.groovybot.bot.commands.settings;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;

public class DuplicatesCommand extends Command {

    public DuplicatesCommand() {
        super(new String[]{"duplicates", "duplicate", "dups"}, CommandCategory.SETTINGS, Permissions.djMode(), "Settings related to duplicates in the queue.", "");
        this.registerSubCommand(new NoDuplicatesCommand());
        this.registerSubCommand(new RemoveDuplicatesCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return sendHelp();
    }

    public class NoDuplicatesCommand extends SubCommand {

        public NoDuplicatesCommand() {
            super(new String[]{"prevent", "no", "deny"}, Permissions.tierOne(), "Toggles the option whether you want to queue duplicated songs or not.", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (event.getGroovyGuild().isPreventDups()) {
                event.getGroovyGuild().setPreventDups(false);
                return send(success(event.translate("command.preventdups.disabled.title"), event.translate("command.preventdups.disabled.description")));

            } else {
                event.getGroovyGuild().setPreventDups(true);
                return send(success(event.translate("command.preventdups.enabled.title"), event.translate("command.preventdups.enabled.description")));
            }
        }
    }

    public class RemoveDuplicatesCommand extends SubCommand {

        public RemoveDuplicatesCommand() {
            super(new String[]{"remove", "rm"}, Permissions.djMode(), "Removes all duplicates from the queue.", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
                return send(error(event.translate("phrases.notinchannel.title"), event.translate("phrases.notinchannel.description")));
            if (!event.getGuild().getSelfMember().getVoiceState().getChannel().equals(event.getMember().getVoiceState().getChannel()))
                return send(error(event.translate("phrases.notsamechannel.title"), event.translate("phrases.notsamechannel.description")));
            MusicPlayer musicPlayer = GroovyBot.getInstance().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());
            int dups = musicPlayer.removeDups();
            return send(success(event.translate("command.dups.removed.title"), String.format(event.translate("command.dups.removed.description"), dups)));
        }
    }
}
