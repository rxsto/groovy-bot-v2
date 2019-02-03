/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.entity.entities.GroovyUser;
import co.groovybot.bot.core.premium.PremiumManager;
import co.groovybot.bot.util.FormatUtil;

public class VoteCommand extends Command {
    public VoteCommand() {
        super(new String[]{"vote", "upvote"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you information about voting", "");
        registerSubCommand(new CheckCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(small(String.format("**[%s](https://vote.groovybot.co)**", event.translate("command.vote"))));
    }

    private static class CheckCommand extends SubCommand {

        CheckCommand() {
            super(new String[]{"check", "activate"}, Permissions.everyone(), "Lets you activate your premium trial", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            final GroovyUser groovyUser = event.getGroovyUser();
            if (!PremiumManager.hasVoted(groovyUser))
                return send(error(event.translate("phrases.error"), event.translate("command.vote.not")));
            if (PremiumManager.hasAlreadyVoted(groovyUser))
                return send(error(event.translate("phrases.error"), event.translate("command.vote.already")));
            if (!PremiumManager.isAbleToVote(groovyUser))
                return send(error(event.translate("phrases.error"), String.format(event.translate("command.vote.forbidden"), FormatUtil.formatDuration(PremiumManager.getVoteAgainIn(event.getGroovyUser())))));
            PremiumManager.givePremium(groovyUser);
            return send(success(event.translate("phrases.success"), String.format(event.translate("command.vote.success"), "1h")));
        }
    }
}
