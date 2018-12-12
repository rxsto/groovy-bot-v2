package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.entity.User;
import co.groovybot.bot.core.premium.PremiumManager;

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
                return send(error(event.translate("command.vote.not.title"), event.translate("command.vote.not.description")));
            if (PremiumManager.hasAlreadyVoted(groovyUser))
                return send(error(event.translate("command.vote.already.title"), event.translate("command.vote.already.description")));
            if (!PremiumManager.isAbleToVote(groovyUser))
                return send(error(event.translate("command.vote.forbidden.title"), event.translate("command.vote.forbidden.description")));
            PremiumManager.givePremium(groovyUser);
            return send(success(event.translate("command.vote.success.title"), event.translate("command.vote.success.description")));
        }
    }
}
