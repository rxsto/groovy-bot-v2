package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class SponsorCommand extends Command {
    public SponsorCommand() {
        super(new String[] {"sponsore"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you some information about our sponsor", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(info(event.translate("command.sponsor.title"), event.translate("command.sponsor.description")));
    }
}
