package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.PremiumManager;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.entity.User;

import java.util.concurrent.TimeUnit;

public class VotedCommand extends Command {

    public VotedCommand() {
        super(new String[]{"voted"}, CommandCategory.GENERAL, Permissions.votedOnly(), "Get patreon features for free", "");
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
