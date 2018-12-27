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

import co.groovybot.bot.core.monitoring.ActionMonitor;
import com.sun.management.OperatingSystemMXBean;
import io.prometheus.client.Gauge;

import java.lang.management.ManagementFactory;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class SystemMonitor extends ActionMonitor {

    private final Gauge memoryUsed = Gauge.build().help("Show's used memory").namespace("groovy").name("memory_used").register();
    private final Gauge memoryFree = Gauge.build().help("Show's free memory").namespace("groovy").name("memory_free").register();
    private final Gauge cpuLoad = Gauge.build().help("Show's cpu load in percentage (0.0 - 1.0)").namespace("groovy").name("cpu_load").register();
    private final Gauge threads = Gauge.build().namespace("groovy").name("thread_count").help("Show's thread count").register();

    private final OperatingSystemMXBean system = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);

    @Override
    public void action() {
        Runtime runtime = Runtime.getRuntime();
        memoryUsed.set(runtime.totalMemory() - runtime.freeMemory());
        memoryFree.set(runtime.freeMemory());
        cpuLoad.set(system.getProcessCpuLoad());
        threads.set(ManagementFactory.getThreadMXBean().getThreadCount());
    }
}
