package io.groovybot.bot.commands.settings;

import io.groovybot.bot.core.command.*;
import io.groovybot.bot.core.command.permission.Permissions;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class BotChannelCommand extends Command {

    public BotChannelCommand() {
        super(new String[]{"botchannel", "bc"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Lets you set a specific channel that will be the only one that is useable for commands", "[#channel]");
        registerSubCommand(new DisableCommand());
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
                event.getGroovyGuild().setCommandsChannel(mentionedChannels.get(0));
                return send(success(event.translate("command.botchannel.title"), String.format(event.translate("command.botchannel.description"), mentionedChannels.get(0))));
            }
        }
    }

    private class DisableCommand extends SubCommand {

        public DisableCommand() {
            super(new String[]{"disable", "reset", "off", "false"}, Permissions.adminOnly(), "Disables the botchannel", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (!event.getGroovyGuild().hasCommandsChannel())
                return send(error(event.translate("command.botchannel.no.channel.title"), event.translate("command.botchannel.no.channel.description")));
            TextChannel oldBotChannel = event.getGroovyGuild().getCommandsChannel();
            event.getGroovyGuild().setCommandsChannel(null);
            return send(success(event.translate("command.botchannel.disable.title"), String.format(event.translate("command.botchannel.disable.description"), oldBotChannel.getAsMention())));
        }
    }
}
