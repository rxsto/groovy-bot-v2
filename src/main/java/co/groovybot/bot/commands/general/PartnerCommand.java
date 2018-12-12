package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;

public class PartnerCommand extends Command {
    public PartnerCommand() {
        super(new String[]{"partner"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you Groovy's partne", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(info(event.translate("command.partner.title"), event.translate("command.partner.description")));
    }
}
