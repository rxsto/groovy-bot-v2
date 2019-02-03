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

package co.groovybot.bot.commands.settings;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SemiInChannelSubCommand;
import co.groovybot.bot.core.entity.entities.GroovyGuild;

public class DuplicatesCommand extends Command {

    public DuplicatesCommand() {
        super(new String[]{"duplicates", "dups"}, CommandCategory.SETTINGS, Permissions.everyone(), "Settings related to duplicates in the queue", "");
        this.registerSubCommand(new NoDuplicatesCommand());
        this.registerSubCommand(new RemoveDuplicatesCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return sendHelp();
    }

    public static class NoDuplicatesCommand extends SubCommand {

        NoDuplicatesCommand() {
            super(new String[]{"prevent", "no", "deny"}, Permissions.tierOne(), "Toggles the option whether you want to queue duplicated songs or not", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            GroovyGuild groovyGuild = event.getGroovyGuild();
            groovyGuild.setPreventDups(!groovyGuild.isPreventDups());
            return send(success(event.translate("phrases.success"), String.format(event.translate("command.noduplicates"), groovyGuild.isPreventDups() ? event.translate("phrases.text.enabled") : event.translate("phrases.text.disabled"))));
        }
    }

    public static class RemoveDuplicatesCommand extends SemiInChannelSubCommand {

        RemoveDuplicatesCommand() {
            super(new String[]{"remove", "rm"}, Permissions.djMode(), "Removes all duplicates from the queue.", "");
        }

        @Override
        protected Result executeCommand(String[] args, CommandEvent event, MusicPlayer player) {
            if (!player.isPlaying())
                return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));

            int dups = player.removeDups();
            return send(success(event.translate("phrases.success"), String.format(event.translate("command.removeduplicates"), dups)));
        }
    }
}
