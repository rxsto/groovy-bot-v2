package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class PartnerCommand extends Command {
    public PartnerCommand() {
        super(new String[]{"partner", "partners"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you Groovy's partnes", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(info(event.translate("command.partner.title"), event.translate("command.partner.description")));
    }
}
