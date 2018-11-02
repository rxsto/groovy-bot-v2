package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.PremiumManager;
import io.groovybot.bot.core.command.*;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.entity.User;

import java.util.concurrent.TimeUnit;

public class VoteCommand extends Command {
    public VoteCommand() {
        super(new String[]{"vote", "upvote"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you information about voting", "");
        registerSubCommand(new CheckCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(info(event.translate("command.vote.title"), event.translate("command.vote.description")));
    }

    private class CheckCommand extends SubCommand {

        public CheckCommand() {
            super(new String[]{"check", "activate"}, Permissions.votedOnly(), "Lets you activate your premium trial", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            final PremiumManager premiumManager = event.getBot().getPremiumManager();
            final User groovyUser = event.getGroovyUser();
            if (!premiumManager.isAllowed(groovyUser))
                return send(error(event.translate("command.voted.notpermitted.title"), event.translate("command.voted.notpermitted.description")));
            event.getBot().getPremiumManager().givePremium(groovyUser, 1, TimeUnit.HOURS);
            return send(info(event.translate("command.voted.title"), event.translate("command.voted.description")));
        }
    }
}
