package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class VoteCommand extends Command {
    public VoteCommand() {
        super(new String[] {"vote", "upvote"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you some information about upvoting", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(info(event.translate("command.vote.title"), event.translate("command.vote.description")));
    }
}
