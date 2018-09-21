package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class PingCommand extends Command {

    public PingCommand() {
        super(new String[] {"ping"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows the bot's current ping", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(info("My ping", String.format("My ping is `%s`", event.getGroovyBot().getShardManager().getAveragePing())));
    }
}
