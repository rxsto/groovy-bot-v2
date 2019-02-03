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
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class BlackListCommand extends Command {

    public BlackListCommand() {
        super(new String[]{"blacklist", "bl"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Lets you block specific textchannels", "");
        registerSubCommand(new AddCommand());
        registerSubCommand(new RemoveCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (args.length == 0)
            if (event.getGroovyGuild().hasBlacklistedChannels()) {
                GroovyGuild groovyGuild = event.getBot().getGuildCache().get(event.getGuild().getIdLong());

                if (!groovyGuild.hasBlacklistedChannels())
                    return send(error(event.translate("phrases.error"), event.translate("command.blacklist.nochannels")));

                StringBuilder channelNames = new StringBuilder();
                groovyGuild.getBlacklistedChannels().forEach(channelObject -> {
                    long channelId = Long.parseLong(channelObject.toString());
                    TextChannel channel = event.getGuild().getTextChannelById(channelId);

                    if (channel == null) {
                        groovyGuild.unBlacklistChannel(channelId);
                        return;
                    }

                    channelNames.append(channel.getAsMention()).append(", ");
                });

                if (channelNames.toString().equals(""))
                    return send(error(event.translate("phrases.error"), event.translate("command.blacklist.nochannels")));

                channelNames.replace(channelNames.lastIndexOf(", "), channelNames.lastIndexOf(", ") + 1, "");
                return send(info(event.translate("phrases.info"), channelNames.toString()));
            }
        return sendHelp();
    }

    private static class AddCommand extends SubCommand {

        AddCommand() {
            super(new String[]{"add"}, Permissions.adminOnly(), "Lets you add a textchannel to the blacklist", "<#channel>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            final List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();

            if (mentionedChannels.isEmpty())
                return sendHelp();

            TextChannel target = mentionedChannels.get(0);
            GroovyGuild groovyGuild = event.getBot().getGuildCache().get(event.getGuild().getIdLong());

            if (groovyGuild.isChannelBlacklisted(target.getIdLong()))
                return send(error(event.translate("phrases.error"), event.translate("command.blacklist.alreadyblacklisted")));

            if (groovyGuild.getBotChannel() == target.getIdLong())
                return send(error(event.translate("phrases.error"), event.translate("command.blacklist.isbotchannel")));

            groovyGuild.blacklistChannel(target.getIdLong());
            return send(success(event.translate("phrases.success"), String.format(event.translate("command.blacklist.added"), target.getAsMention())));
        }
    }

    private static class RemoveCommand extends SubCommand {

        RemoveCommand() {
            super(new String[]{"remove", "rm"}, Permissions.adminOnly(), "Lets you remove a textchannel from the blacklist", "<#channel>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            final List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();

            if (mentionedChannels.isEmpty())
                return sendHelp();

            TextChannel target = mentionedChannels.get(0);
            GroovyGuild groovyGuild = event.getBot().getGuildCache().get(event.getGuild().getIdLong());

            if (!groovyGuild.isChannelBlacklisted(target.getIdLong()))
                return send(error(event.translate("phrases.error"), event.translate("command.blacklist.notblacklisted")));

            groovyGuild.unBlacklistChannel(target.getIdLong());
            return send(success(event.translate("phrases.success"), String.format(event.translate("command.blacklist.removed"), target.getAsMention())));
        }
    }
}
