package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.premium.PremiumManager;
import io.groovybot.bot.core.command.*;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.entity.User;

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
            super(new String[]{"check", "activate"}, Permissions.everyone(), "Lets you activate your premium trial", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            final User groovyUser = event.getGroovyUser();
            if (!PremiumManager.hasVoted(groovyUser))
                return send(error(event.translate("command.voted.not.title"), event.translate("command.voted.not.description")));
            if (PremiumManager.hasAlreadyVoted(groovyUser))
                return send(error(event.translate("command.voted.already.title"), event.translate("command.voted.already.description")));
            if (!PremiumManager.isAbleToVote(groovyUser))
                return send(error(event.translate("command.voted.forbidden.title"), event.translate("command.voted.forbidden.description")));
            PremiumManager.givePremium(groovyUser);
            return send(success(event.translate("command.voted.title"), event.translate("command.voted.description")));
        }
    }
}
