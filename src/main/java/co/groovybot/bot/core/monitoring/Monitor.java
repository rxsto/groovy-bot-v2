package co.groovybot.bot.core.monitoring;

import org.influxdb.dto.Point;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public abstract class Monitor {
    public abstract Point save();
}
