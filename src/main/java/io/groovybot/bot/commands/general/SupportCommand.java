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
        return send(noTitle(String.format("**[%s](https://discord.gg/5s5TsW2)**", event.translate("command.support"))));
    }
}
