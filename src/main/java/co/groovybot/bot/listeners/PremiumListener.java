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

package co.groovybot.bot.listeners;

import co.groovybot.bot.core.premium.PremiumHandler;
import co.groovybot.bot.core.premium.Tier;
import co.groovybot.bot.util.PremiumUtil;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class PremiumListener {

    private final PremiumHandler premiumHandler;

    public PremiumListener(PremiumHandler premiumHandler) {
        this.premiumHandler = premiumHandler;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onRoleAdd(GuildMemberRoleAddEvent event) {
        if (event.getGuild().getIdLong() != 403882830225997825L) return;
        Tier tier = PremiumUtil.getTier(event.getMember(), event.getGuild());
        if (tier == Tier.NONE) return;
        premiumHandler.addPatron(event.getUser().getIdLong(), tier);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onRoleRemove(GuildMemberRoleRemoveEvent event) {
        if (event.getGuild().getIdLong() != 403882830225997825L) return;
        if (PremiumUtil.hasPremiumRole(event.getRoles()))
            premiumHandler.removePatron(event.getUser().getIdLong());
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onLeave(GuildMemberLeaveEvent event) {
        if (event.getGuild().getIdLong() != 403882830225997825L) return;
        if (PremiumUtil.hasPremiumRole(event.getMember().getRoles()))
            premiumHandler.removePatron(event.getUser().getIdLong());
    }
}
