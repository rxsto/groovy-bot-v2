package co.groovybot.bot.core.monitoring.monitors;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.monitoring.Monitor;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.influxdb.dto.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class MessageMonitor extends Monitor {

    private final List<Integer> lengths;
    private int counter;

    public MessageMonitor() {
        this.counter = 0;
        this.lengths = new ArrayList<>();
        GroovyBot.getInstance().getEventManager().register(this);
    }

    @Override
    public Point save() {
        Point point = Point.measurement("messages")
                .addField("count", counter)
                .addField("average_length", average(lengths))
                .build();

        lengths.clear();
        counter = 0;
        return point;
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
