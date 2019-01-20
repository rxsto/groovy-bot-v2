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

package co.groovybot.bot.util;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.premium.Tier;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;

public class PremiumUtil {

    public static Tier getTier(Member member, Guild guild) {
        if (member == null || guild == null)
            return Tier.NONE;

        if (member.getRoles().contains(guild.getRoleById(525727475364265985L)))
            return Tier.ONE;
        if (member.getRoles().contains(guild.getRoleById(525727525037408260L)))
            return Tier.TWO;
        if (member.getRoles().contains(guild.getRoleById(525727573301526538L)))
            return Tier.THREE;

        return Tier.NONE;
    }

    public static boolean hasPremiumRole(List<Role> roles) {
        return roles.contains(GroovyBot.getInstance().getSupportGuild().getRoleById(525727475364265985L)) || roles.contains(GroovyBot.getInstance().getSupportGuild().getRoleById(525727525037408260L)) || roles.contains(GroovyBot.getInstance().getSupportGuild().getRoleById(525727573301526538L));
    }
}
