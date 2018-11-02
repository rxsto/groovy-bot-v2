package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class SupportCommand extends Command {
    public SupportCommand() {
        super(new String[]{"support", "sup"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you an invite to Groovy's official guild", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(info(event.translate("command.support.title"), event.translate("command.support.description")));
    }
}
