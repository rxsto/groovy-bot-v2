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

package co.groovybot.bot.core.entity;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.entity.entities.GroovyGuild;
import co.groovybot.bot.core.entity.entities.GroovyUser;

public class EntityProvider {

    public static GroovyUser getUser(Long entityId) {
        return GroovyBot.getInstance().getUserCache().get(entityId);
    }

    public static GroovyGuild getGuild(Long entityId) {
        return GroovyBot.getInstance().getGuildCache().get(entityId);
    }
}

