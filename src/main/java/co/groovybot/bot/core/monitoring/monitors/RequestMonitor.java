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
import net.dv8tion.jda.core.events.http.HttpRequestEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.influxdb.dto.Point;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class RequestMonitor extends Monitor {

    // TODO: Implement request count for every shard separately
    // private Map<Integer, Integer> requests; // ShardId, RequestCount
    private int counter;

    public RequestMonitor() {
        //this.requests = new HashMap<>();
        GroovyBot.getInstance().getEventManager().register(this);
    }

    @Override
    public Point save() {
        Point.Builder point = Point.measurement("requests")
                .addField("count", counter);
        counter = 0;

        return point.build();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    private void onRequest(HttpRequestEvent event) {
        counter++;
    }
}
