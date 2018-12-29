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

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;
import co.groovybot.bot.core.entity.Guild;

public class AutoLeaveCommand extends SameChannelCommand {
    public AutoLeaveCommand() {
        super(new String[]{"autoleave", "al"}, CommandCategory.SETTINGS, Permissions.tierTwo(), "Lets you deactivate the auto-leave mode", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR))
            return send(error(event.translate("phrases.nopermission.title"), event.translate("phrases.nopermission.admin")));
        else {
            MusicPlayer player = event.getBot().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());
            if (event.getGroovyGuild().isAutoLeave()) {
                event.getGroovyGuild().setAutoLeave(false);
                player.cancelLeaveListener();
                return send(success(event.translate("command.autoleave.disabled.title"), event.translate("command.autoleave.disabled.description")));
            } else {
                event.getGroovyGuild().setAutoLeave(true);
                player.scheduleLeaveListener();
                return send(success(event.translate("command.autoleave.enabled.title"), event.translate("command.autoleave.enabled.description")));
            }
        }
    }
}
