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
import co.groovybot.bot.core.monitoring.ActionMonitor;
import io.prometheus.client.Gauge;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class MessageMonitor implements ActionMonitor {

    private final Gauge counterGauge;
    private final Gauge lengthGauge;

    private final List<Integer> lengths;
    private int counter;

    public MessageMonitor() {

        counterGauge = Gauge.build().namespace("groovy").name("message_count").help("Show's the message count").register();
        lengthGauge = Gauge.build().namespace("groovy").name("average_message_length").help("Show's the average message length").register();

        this.counter = 0;
        this.lengths = new ArrayList<>();
        GroovyBot.getInstance().getEventManager().register(this);
    }

    @Override
    public void action() {
        counterGauge.set(counter);
        lengthGauge.set(average(lengths));

        lengths.clear();
        counter = 0;
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
