package co.groovybot.bot.listeners;

import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class YanningListener {

    @SubscribeEvent
    public void yanning(VoiceChannelCreateEvent event) {
        System.out.println("It works.");
    }

}
