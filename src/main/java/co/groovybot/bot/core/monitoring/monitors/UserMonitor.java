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
