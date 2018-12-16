package co.groovybot.bot.core.monitoring.monitors;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.LavalinkManager;
import co.groovybot.bot.core.monitoring.Monitor;
import org.influxdb.dto.Point;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class GuildMonitor extends Monitor {
    @Override
    public Point save() {
        return Point.measurement("guilds")
                .addField("guild_count", GroovyBot.getInstance().getShardManager().getGuildCache().size())
                .addField("playing_guilds", LavalinkManager.countPlayers())
                .build();
    }
}
