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

import co.groovybot.bot.core.monitoring.Monitor;
import com.sun.management.OperatingSystemMXBean;
import org.influxdb.dto.Point;

import java.lang.management.ManagementFactory;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class SystemMonitor extends Monitor {

    private final OperatingSystemMXBean system = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);

    @Override
    public Point save() {
        Runtime runtime = Runtime.getRuntime();
        return Point.measurement("system_info")
                //.addField("memory_used", FormatUtil.parseBytes(system.getTotalPhysicalMemorySize() - system.getFreePhysicalMemorySize()))
                .addField("memory_used", runtime.totalMemory() - runtime.freeMemory())
                .addField("memory_free", system.getFreePhysicalMemorySize())
                .addField("cpu_load", system.getSystemCpuLoad())
                .build();
    }
}
