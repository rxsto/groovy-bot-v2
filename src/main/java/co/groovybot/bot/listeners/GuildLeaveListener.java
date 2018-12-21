package co.groovybot.bot.listeners;

import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@SuppressWarnings("unused")
public class GuildLeaveListener {

    @SubscribeEvent
    private void handleGuildKick(GuildLeaveEvent event) {
        event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("We're sorry that Groovy couldn't fulfill your expectations! If there is anything we could do better let us know on our Discord guild: https://look-at.it/groovysupport").queue(ignored -> {}, ignored2 -> {}));
    }

}
