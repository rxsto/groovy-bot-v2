package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class DonateCommand extends Command {
    public DonateCommand() {
        super(new String[] {"donate"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you some information about donating", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return null;
    }
}
