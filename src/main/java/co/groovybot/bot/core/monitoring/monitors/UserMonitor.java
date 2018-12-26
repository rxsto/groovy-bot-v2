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

package co.groovybot.bot.core.monitoring.monitors;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.monitoring.Monitor;
import org.influxdb.dto.Point;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class UserMonitor extends Monitor {

    @Override
    public Point save() {
        return Point.measurement("users")
                .addField("groovy_guild_users", GroovyBot.getInstance().getShardManager().getGuildById("403882830225997825").getMemberCache().size())
                .addField("user_count", GroovyBot.getInstance().getShardManager().getUserCache().size()).build();
    }
}
