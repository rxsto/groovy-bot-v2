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

package co.groovybot.bot.core.monitoring;

import lombok.extern.log4j.Log4j2;
import org.influxdb.InfluxDB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class MonitorManager implements Runnable {

    private final List<Monitor> monitors;
    private final ScheduledExecutorService executorService;
    private final InfluxDB influxDB;

    public MonitorManager(InfluxDB influxDB) {
        this.influxDB = influxDB;
        this.monitors = new ArrayList<>();
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    public void start() {
        executorService.scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);
    }

    public void register(Monitor... monitors) {
        if (monitors == null) {
            log.warn("[MonitorManager] Passed monitors are null!");
            return;
        }
        this.monitors.addAll(Arrays.asList(monitors));
    }

    @Override
    public void run() {
        log.debug("[MonitorManager] Posting stats to InfluxDB.");
        monitors.forEach(m -> influxDB.write(m.save()));
    }
}
