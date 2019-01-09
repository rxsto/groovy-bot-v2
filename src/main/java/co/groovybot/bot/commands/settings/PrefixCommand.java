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

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.entity.entities.GroovyGuild;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super(new String[]{"prefix", "pr"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Lets you set Groovy's prefix", "[prefix]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        GroovyGuild groovyGuild = event.getGroovyGuild();

        if (args.length == 0)
            return send(small(String.format(event.translate("command.prefix.current"), groovyGuild.getPrefix())));

        String current = groovyGuild.getPrefix();
        groovyGuild.setPrefix(args[0]);
        return send(success(event.translate("phrases.success"), String.format(event.translate("command.prefix"), current, groovyGuild.getPrefix())));
    }
}
