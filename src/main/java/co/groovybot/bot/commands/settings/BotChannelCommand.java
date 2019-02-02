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
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class BotChannelCommand extends Command {

    public BotChannelCommand() {
        super(new String[]{"botchannel", "bc"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Lets you set Groovy's botchannel", "<#channel>");
        registerSubCommand(new SetCommand());
        registerSubCommand(new DisableCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return sendHelp();
    }

    private static class SetCommand extends SubCommand {

        SetCommand() {
            super(new String[]{"set"}, Permissions.adminOnly(), "Lets you set the botchannel", "<#channel>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length == 0)
                return sendHelp();
            else {
                final List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();

                if (mentionedChannels.isEmpty())
                    return sendHelp();
                else {
                    if (event.getGroovyGuild().getBlacklistedChannels().contains(mentionedChannels.get(0)))
                        return send(error(event.translate("phrases.error"), String.format(event.translate("command.botchannel.blacklisted"), mentionedChannels.get(0).getAsMention())));
                    event.getGroovyGuild().setBotChannel(mentionedChannels.get(0).getIdLong());
                    return send(success(event.translate("phrases.success"), String.format(event.translate("command.botchannel"), mentionedChannels.get(0).getAsMention())));
                }
            }
        }
    }

    private static class DisableCommand extends SubCommand {

        DisableCommand() {
            super(new String[]{"disable"}, Permissions.adminOnly(), "Disables the botchannel", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (!event.getGroovyGuild().hasCommandsChannel())
                return send(error(event.translate("phrases.error"), event.translate("command.botchannel.nochannel")));

            TextChannel oldBotChannel = event.getBot().getShardManager().getTextChannelById(event.getGroovyGuild().getBotChannel());
            event.getGroovyGuild().setBotChannel(0L);
            return send(success(event.translate("phrases.success"), String.format(event.translate("command.botchannel.disable"), oldBotChannel.getAsMention())));
        }
    }
}
