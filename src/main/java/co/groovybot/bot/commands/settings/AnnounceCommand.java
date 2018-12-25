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

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.core.entity.Guild;

public class AnnounceCommand extends Command {

    public AnnounceCommand() {
        super(new String[]{"announce", "announcesongs"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Lets you toggle the announcement-mode", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        Guild guild = EntityProvider.getGuild(event.getGuild().getIdLong());
        if (guild.isAnnounceSongs()) {
            guild.setAnnounceSongs(false);
            return send(success(event.translate("command.announce.disabled.title"), event.translate("command.announce.disabled.description")));
        }
        guild.setAnnounceSongs(true);
        return send(success(event.translate("command.announce.enabled.title"), event.translate("command.announce.enabled.description")));
    }
}
