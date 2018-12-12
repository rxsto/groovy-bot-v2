package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;

public class SupportCommand extends Command {
    public SupportCommand() {
        super(new String[]{"support", "sup"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you an invite to Groovy's official guild", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(noTitle(String.format("**[%s](https://discord.gg/5s5TsW2)**", event.translate("command.support"))));
    }
}
