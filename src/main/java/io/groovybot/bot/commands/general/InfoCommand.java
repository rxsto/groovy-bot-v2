package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class InfoCommand extends Command {
    public InfoCommand() {
        super(new String[] {"info"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you some useful information", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return null;
    }
}
