package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class InviteCommand extends Command {
    public InviteCommand() {
        super(new String[] {"invite"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you an invite for Groovy", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(info(event.translate("command.invite.title"), event.translate("command.invite.description")));
    }
}
