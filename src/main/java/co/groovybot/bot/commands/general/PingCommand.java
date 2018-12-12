package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;

public class PingCommand extends Command {

    public PingCommand() {
        super(new String[]{"ping", "latency"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows the Groovy's current ping", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(noTitle(String.format("**%s** ms", Math.ceil(event.getBot().getShardManager().getAveragePing()))));
    }
}
