package io.groovybot.bot.commands.settings;

import io.groovybot.bot.core.command.*;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.entity.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class BlackListCommand extends Command {

    public BlackListCommand() {
        super(new String[]{"blacklist", "bl"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Block commands in some channels", "");
        registerSubCommand(new ListCommand());
        registerSubCommand(new AddCommand());
        registerSubCommand(new RemoveCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (args.length == 0)
            return sendHelp();
        return null;
    }

    private class AddCommand extends SubCommand {

        public AddCommand() {
            super(new String[]{"add", "block"}, Permissions.adminOnly(), "Let's you block commands in a channel", "<#channel>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            final List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();
            if (mentionedChannels.isEmpty())
                return sendHelp();
            TextChannel target = mentionedChannels.get(0);
            Guild guild = event.getBot().getGuildCache().get(event.getGuild().getIdLong());
            if (guild.isChannelBlacklisted(target.getIdLong()))
                return send(error(event.translate("command.blacklist.alreadyblacklisted.title"), event.translate("command.blacklist.alreadyblacklisted.description")));
            guild.blacklistChannel(target.getIdLong());
            return send(success(event.translate("command.blacklist.added.title"), String.format(event.translate("command.blacklist.added.description"), target.getName())));
        }
    }

    private class RemoveCommand extends SubCommand {

        public RemoveCommand() {
            super(new String[]{"remove", "rm", "unblock"}, Permissions.adminOnly(), "Unblocks commands from a channel", "<#channel>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            final List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();
            if (mentionedChannels.isEmpty())
                return sendHelp();
            TextChannel target = mentionedChannels.get(0);
            Guild guild = event.getBot().getGuildCache().get(event.getGuild().getIdLong());
            if (!guild.isChannelBlacklisted(target.getIdLong()))
                return send(error(event.translate("command.blacklist.notblacklisted.title"), event.translate("command.blacklist.notblacklisted.description")));
            guild.unBlacklistChannel(target.getIdLong());
            return send(success(event.translate("command.blacklist.removed.title"), String.format(event.translate("command.blacklist.removed.description"), target.getName())));
        }
    }

    private class ListCommand extends SubCommand {

        public ListCommand() {
            super(new String[]{"list", "display"}, Permissions.adminOnly(), "Displays a list of all blacklisted channels", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            Guild guild = event.getBot().getGuildCache().get(event.getGuild().getIdLong());
            if (!guild.hasBlacklistedChannels())
                return send(error(event.translate("command.blacklist.nochannels.title"), event.translate("command.blacklist.nochannels.description")));
            StringBuilder channelNames = new StringBuilder();
            guild.getBlacklistedChannels().forEach(channelObject -> {
                long channelId = Long.valueOf(channelObject.toString());
                TextChannel channel = event.getGuild().getTextChannelById(channelId);
                if (channel == null) {
                    guild.unBlacklistChannel(channelId);
                    return;
                }
                channelNames.append(channel.getAsMention()).append(",");
            });
            channelNames.replace(channelNames.lastIndexOf(","), channelNames.lastIndexOf(",") + 1, "");
            return send(info(event.translate("command.blacklist.list.title"), String.format(event.translate("command.blacklist.list.description"), channelNames.toString())));
        }
    }
}
