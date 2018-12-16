package co.groovybot.bot.core.monitoring;

import lombok.extern.log4j.Log4j2;
import org.influxdb.InfluxDB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
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

    public void register(@NotNull Monitor... monitors) {
        this.monitors.addAll(Arrays.asList(monitors));
    }

    @Override
    public void run() {
        log.debug("[MonitorManager] Posting stats to InfluxDB.");
        monitors.forEach(m -> influxDB.write(m.save()));
    }
}
