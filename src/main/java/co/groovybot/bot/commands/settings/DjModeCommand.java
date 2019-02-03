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

import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.entity.entities.GroovyGuild;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;

public class DjModeCommand extends Command {

    public DjModeCommand() {
        super(new String[]{"djmode", "dj", "setdj"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Lets you toggle the dj-mode", "");
        this.registerSubCommand(new DjRoleCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        GroovyGuild groovyGuild = event.getGroovyGuild();
        groovyGuild.setDjMode(!groovyGuild.isDjMode());
        return send(success(event.translate("phrases.success"), String.format(event.translate("command.djmode"), groovyGuild.isDjMode() ? event.translate("phrases.text.enabled") : event.translate("phrases.text.disabled"))));
    }

    public static class DjRoleCommand extends SubCommand {

        DjRoleCommand() {
            super(new String[]{"role", "setrole"}, Permissions.adminOnly(), "Lets you set the dj-role", "<role-name>/<@role>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length == 0)
                return sendHelp();

            GroovyGuild groovyGuild = event.getGroovyGuild();

            if (!groovyGuild.isDjMode())
                return send(error(event.translate("phrases.error"), event.translate("command.djmode.disabled")));

            if (event.getMessage().getMentionedRoles().isEmpty()) {
                List<Role> roles = event.getGuild().getRolesByName(args[0], true);

                if (roles.isEmpty())
                    return send(error(event.translate("phrases.error"), event.translate("phrases.invalid.role")));

                Role role = roles.get(0);

                if (role == null)
                    return send(error(event.translate("phrases.error"), event.translate("phrases.invalid.role")));

                groovyGuild.setDjRole(role.getIdLong());
                return send(success(event.translate("phrases.success"), String.format(event.translate("command.djmode.role"), role.getName())));
            } else {
                groovyGuild.setDjRole(event.getMessage().getMentionedRoles().get(0).getIdLong());
                return send(success(event.translate("phrases.success"), String.format(event.translate("command.djmode.role"), event.getMessage().getMentionedRoles().get(0).getName())));
            }
        }
    }
}
