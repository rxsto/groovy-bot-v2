/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergeij Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
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
