package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.FormatUtil;

public class UptimeCommand extends Command {
    public UptimeCommand() {
        super(new String[]{"uptime", "up"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you some Groovy's uptime", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(info(event.translate("command.uptime.title"), String.format(event.translate("command.uptime.description"), FormatUtil.parseUptime(System.currentTimeMillis() - event.getBot().getStartupTime()))));
    }
}
