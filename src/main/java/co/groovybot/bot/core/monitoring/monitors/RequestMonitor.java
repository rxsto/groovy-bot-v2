package co.groovybot.bot.core.monitoring.monitors;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.monitoring.Monitor;
import net.dv8tion.jda.core.events.http.HttpRequestEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.influxdb.dto.Point;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class RequestMonitor extends Monitor {

    // TODO: Implement request count for every shard separately
    //private Map<Integer, Integer> requests; // ShardId, RequestCount
    private int counter;

    public RequestMonitor() {
        //this.requests = new HashMap<>();
        GroovyBot.getInstance().getEventManager().register(this);
    }

    @Override
    public Point save() {
        Point.Builder point = Point.measurement("requests")
                .addField("count", counter);
        counter = 0;

        return point.build();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    private void onRequest(HttpRequestEvent event) {
        counter++;
    }
}
