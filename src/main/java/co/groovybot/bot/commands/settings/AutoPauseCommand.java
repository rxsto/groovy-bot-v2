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
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;
import co.groovybot.bot.core.entity.entities.GroovyGuild;

public class AutoPauseCommand extends SameChannelCommand {

    public AutoPauseCommand() {
        super(new String[]{"autopause"}, CommandCategory.SETTINGS, Permissions.tierOne(), "If this option is enabled the bot will stop playing music while nobody is in the channel", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        GroovyGuild groovyGuild = event.getGroovyGuild();
        groovyGuild.setAutoPause(!groovyGuild.isAutoPause());
        return send(success(event.translate("phrases.success"), String.format(event.translate("command.autopause"), groovyGuild.isAutoPause() ? event.translate("phrases.text.enabled") : event.translate("phrases.text.disabled"))));
    }
}
