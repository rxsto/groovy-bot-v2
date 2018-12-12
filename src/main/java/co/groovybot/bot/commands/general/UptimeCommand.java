package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.util.FormatUtil;

public class UptimeCommand extends Command {
    public UptimeCommand() {
        super(new String[]{"uptime", "up"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you Groovy's uptime", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(noTitle(String.format("<:online:449207830105554964> " + event.translate("command.uptime.description"), FormatUtil.parseUptime(System.currentTimeMillis() - event.getBot().getStartupTime()))));
    }
}
