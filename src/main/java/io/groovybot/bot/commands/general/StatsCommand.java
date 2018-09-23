package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class StatsCommand extends Command {
    public StatsCommand() {
        super(new String[] {"stats"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you Groovy's current stats", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return null;
    }
}
