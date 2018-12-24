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

import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.core.entity.Guild;

public class DjModeCommand extends Command {

    public DjModeCommand() {
        super(new String[]{"setdj", "dj", "djmode"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Lets you toggle the dj-mode", "");
        this.registerSubCommand(new DjRoleCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        Guild guild = EntityProvider.getGuild(event.getGuild().getIdLong());
        if (!guild.isDjMode()) {
            guild.setDjMode(true);
            return send(success(event.translate("command.dj.enabled.title"), event.translate("command.dj.enabled.description")));
        }
        guild.setDjMode(false);
        return send(success(event.translate("command.dj.disabled.title"), event.translate("command.dj.disabled.description")));
    }

    public class DjRoleCommand extends SubCommand{

        public DjRoleCommand() {
            super(new String[]{"role"}, Permissions.adminOnly(), "Set the dj-role", "[role]");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            Guild guild = EntityProvider.getGuild(event.getGuild().getIdLong());
            if(!guild.isDjMode())
                return send(error(event.translate("command.dj.not.enabled.title"), event.translate("command.dj.not.enabled.description")));
            if(args.length<1)
                return send(error(event.translate("phrases.invalidarguments.title"), event.translate("phrases.invalidarguments.description")));
            if(event.getMessage().getMentionedRoles().isEmpty()) {
                try {
                    guild.setDjRole(event.getGuild().getRolesByName(args[0],true).get(0).getIdLong());
                    return send(success(event.translate(""), event.translate("")));
                }catch (Exception e) {
                    return send(error(event.translate("phrases.role.not.found.title"), event.translate("phrases.role.not.found.description")));
                }
            }
            return null;
        }
    }
}
