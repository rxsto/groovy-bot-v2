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
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.utils.Helpers;

import java.util.List;

public class AutoJoinCommand extends Command {
    public AutoJoinCommand() {
        super(new String[]{"autojoin", "aj"}, CommandCategory.SETTINGS, Permissions.tierThree(), "Lets you set an AutoJoin channel", "[channelId/name]");
        registerSubCommand(new DisableCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        GroovyGuild groovyGuild = event.getGroovyGuild();

        if (event.noArgs()) {
            if (!groovyGuild.hasAutoJoinChannel())
                return send(error(event.translate("phrases.error"), event.translate("command.autojoin.nochannel")));
            else
                return send(info(event.translate("phrases.info"), String.format(event.translate("command.autojoin.info"), groovyGuild.getAutoJoinChannel().getName())));
        }

        VoiceChannel target;

        if (Helpers.isNumeric(args[0]))
            target = event.getGuild().getVoiceChannelById(args[0]);
        else {
            List<VoiceChannel> foundChannels = event.getGuild().getVoiceChannelsByName(event.getArguments(), true);
            target = foundChannels.isEmpty() ? null : foundChannels.get(0);
        }

        if (target == null)
            return send(error(event.translate("phrases.notfound"), event.translate("command.autojoin.notfound")));

        groovyGuild.setAutoJoinChannel(target);
        return send(success(event.translate("phrases.success"), String.format(event.translate("command.autojoin"), target.getName())));
    }

    private static class DisableCommand extends SubCommand {

        DisableCommand() {
            super(new String[]{"disable", "delete"}, Permissions.adminOnly(), "Lets you disable the AutoJoin channel");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            GroovyGuild groovyGuild = event.getGroovyGuild();

            if (!groovyGuild.hasAutoJoinChannel())
                return send(error(event.translate("phrases.error"), event.translate("command.autojoin.nochannel")));

            groovyGuild.setAutoJoinChannelId(0L);
            return send(success(event.translate("phrases.success"), event.translate("command.autojoin.disable")));
        }
    }
}
