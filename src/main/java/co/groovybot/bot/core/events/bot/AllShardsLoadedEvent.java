package co.groovybot.bot.core.events.bot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.ReadyEvent;

public class AllShardsLoadedEvent extends ReadyEvent {

    public AllShardsLoadedEvent(JDA api, long responseNumber) {
        super(api, responseNumber);
    }
}
