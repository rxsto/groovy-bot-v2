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
import co.groovybot.bot.core.entity.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.utils.Helpers;

import java.util.List;

// TODO: REWORK STRINGS AND MESSAGES

public class AutoJoinCommand extends Command {
    public AutoJoinCommand() {
        super(new String[]{"autojoin", "aj"}, CommandCategory.SETTINGS, Permissions.tierThree(), "Let's you define a channel in which the bot joins automatically when a user joins into it", "[channelId/name]");
        registerSubCommand(new DisableCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        Guild guild = event.getGroovyGuild();
        if (event.noArgs()) {
            if (!guild.hasAutoJoinChannel())
                return send(error(event.translate("command.autojoin.nochannel.title"), event.translate("command.autojoin.nochannel.description")));
            else
                return send(info(event.translate("command.autojoin.info.title"), String.format(event.translate("command.autojoin.info.description"), guild.getAutoJoinChannel().getName())));
        }
        VoiceChannel target;
        if (Helpers.isNumeric(args[0]))
            target = event.getGuild().getVoiceChannelById(args[0]);
        else {
            List<VoiceChannel> foundChannels = event.getGuild().getVoiceChannelsByName(event.getArguments(), true);
            target = foundChannels.isEmpty() ? null : foundChannels.get(0);
        }
        if (target == null)
            return send(error(event.translate("command.autojoin.notfound.title"), event.translate("command.autojoin.notfound.description")));
        guild.setAutoJoinChannel(target);
        return send(success(event.translate("command.autojoin.success.title"), String.format(event.translate("command.autojoin.success.description"), target.getName())));
    }

    private class DisableCommand extends SubCommand {

        private DisableCommand() {
            super(new String[]{"disable", "delete"}, Permissions.adminOnly(), "Let's you disable the current AutoJoin");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            Guild guild = event.getGroovyGuild();
            if (!guild.hasAutoJoinChannel())
                return send(error(event.translate("command.autojoin.nochannel.title"), event.translate("command.autojoin.nochannel.description")));
            guild.setAutoJoinChannelId(0L);
            return send(success(event.translate("command.autojoin.disable.success.title"), event.translate("command.autojoin.disable.success.description")));
        }
    }
}
