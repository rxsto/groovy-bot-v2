package co.groovybot.bot.core.monitoring.monitors;

import co.groovybot.bot.core.monitoring.Monitor;
import co.groovybot.bot.util.FormatUtil;
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
