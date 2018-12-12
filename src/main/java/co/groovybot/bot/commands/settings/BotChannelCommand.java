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
        if (event.getGroovyGuild().hasCommandsChannel())
            return send(info(event.translate("command.botchannel.current.title"), String.format(event.translate("command.botchannel.current.description"), event.getBot().getShardManager().getTextChannelById(event.getGroovyGuild().getBotChannel()).getAsMention())));
        return sendHelp();
    }

    private class SetCommand extends SubCommand {

        public SetCommand() {
            super(new String[]{"set"}, Permissions.adminOnly(), "Sets the botchannel", "<#channel>");
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
                    event.getGroovyGuild().setBotChannel(mentionedChannels.get(0).getIdLong());
                    return send(success(event.translate("command.botchannel.title"), String.format(event.translate("command.botchannel.description"), mentionedChannels.get(0))));
                }
            }
        }
    }

    private class DisableCommand extends SubCommand {

        public DisableCommand() {
            super(new String[]{"disable"}, Permissions.adminOnly(), "Disables the botchannel", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (!event.getGroovyGuild().hasCommandsChannel())
                return send(error(event.translate("command.botchannel.no.channel.title"), event.translate("command.botchannel.no.channel.description")));

            TextChannel oldBotChannel = event.getBot().getShardManager().getTextChannelById(event.getGroovyGuild().getBotChannel());
            event.getGroovyGuild().setBotChannel(0);
            return send(success(event.translate("command.botchannel.disable.title"), String.format(event.translate("command.botchannel.disable.description"), oldBotChannel.getAsMention())));
        }
    }
}
