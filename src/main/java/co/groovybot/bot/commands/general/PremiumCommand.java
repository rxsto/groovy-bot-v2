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

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;

public class PremiumCommand extends Command {

    public PremiumCommand() {
        super(new String[]{"premium", "premiumstatus", "tier"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows your current premium-status", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (Permissions.tierThree().isCovered(event.getPermissions(), event))
            return send(info(event.translate("command.premium.tier.title"), String.format(event.translate("command.premium.tier.description"), "Tier Three")));
        if (Permissions.tierTwo().isCovered(event.getPermissions(), event))
            return send(info(event.translate("command.premium.tier.title"), String.format(event.translate("command.premium.tier.description"), "Tier Two")));
        if (Permissions.tierOne().isCovered(event.getPermissions(), event))
            return send(info(event.translate("command.premium.tier.title"), String.format(event.translate("command.premium.tier.description"), "Tier One")));
        return send(info(event.translate("command.premium.none.title"), event.translate("command.premium.none.description")));
    }
}
