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
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.influxdb.dto.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class MessageMonitor extends Monitor {

    private final List<Integer> lengths;
    private int counter;

    public MessageMonitor() {
        this.counter = 0;
        this.lengths = new ArrayList<>();
        GroovyBot.getInstance().getEventManager().register(this);
    }

    @Override
    public Point save() {
        Point point = Point.measurement("messages")
                .addField("count", counter)
                .addField("average_length", average(lengths))
                .build();

        lengths.clear();
        counter = 0;
        return point;
    }

    private double average(List<Integer> list) {
        return list.stream().mapToInt(i -> i).average().orElse(0D);
    }

    @SubscribeEvent
    private void onMessage(GuildMessageReceivedEvent event) {
        counter++;
        lengths.add(event.getMessage().getContentRaw().length());
    }
}
