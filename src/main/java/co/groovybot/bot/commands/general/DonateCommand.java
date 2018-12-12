package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;

public class DonateCommand extends Command {
    public DonateCommand() {
        super(new String[]{"donate", "patreon"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you a link to Groovy's Patreon", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(info(event.translate("command.donate.title"), event.translate("command.donate.description")));
    }
}
