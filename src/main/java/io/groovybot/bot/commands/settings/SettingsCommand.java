package io.groovybot.bot.commands.settings;

import io.groovybot.bot.core.command.*;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.entity.EntityProvider;
import io.groovybot.bot.core.entity.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class SettingsCommand extends Command {

    public SettingsCommand() {
        super(new String[]{"settings", "set"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Shows you Groovy's settings", "[setting]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return null;
    }

    private class AnnounceCommand extends SubCommand {

        public AnnounceCommand() {
            super(new String[]{"announce", "announcesongs"}, Permissions.adminOnly(), "Lets you toggle the announcement-mode", "");
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

    private class BlackListCommand extends SubCommand {

        public BlackListCommand() {
            super(new String[]{""}, Permissions.adminOnly(), "", "");
            registerSubCommand(new AddCommand());
            registerSubCommand(new RemoveCommand());
            registerSubCommand(new ListCommand());
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            return sendHelp();
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
                if (channelNames.toString().equals(""))
                    return send(error(event.translate("command.blacklist.nochannels.title"), event.translate("command.blacklist.nochannels.description")));

                channelNames.replace(channelNames.lastIndexOf(","), channelNames.lastIndexOf(",") + 1, "");
                return send(info(event.translate("command.blacklist.list.title"), String.format(event.translate("command.blacklist.list.description"), channelNames.toString())));
            }
        }
    }

    private class BotChannelCommand extends SubCommand {

        public BotChannelCommand() {
            super(new String[]{""}, Permissions.adminOnly(), "", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            return null;
        }
    }

    private class DjModeCommand extends SubCommand {

        public DjModeCommand() {
            super(new String[]{""}, Permissions.adminOnly(), "", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            return null;
        }
    }

    private class PrefixCommand extends SubCommand {

        public PrefixCommand() {
            super(new String[]{""}, Permissions.adminOnly(), "", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            return null;
        }
    }
}
