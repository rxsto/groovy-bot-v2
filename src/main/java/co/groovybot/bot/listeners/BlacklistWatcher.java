package co.groovybot.bot.listeners;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.cache.Cache;
import co.groovybot.bot.core.entity.Guild;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class BlacklistWatcher {

    private final Cache<Guild> guildCache;

    @SubscribeEvent
    private void onTextChannelDelete(TextChannelDeleteEvent event) {
        final Guild guild = guildCache.get(event.getGuild().getIdLong());
        final long channelId = event.getChannel().getIdLong();
        if (guild.isChannelBlacklisted(channelId))
            guild.unBlacklistChannel(channelId);
        if (event.getChannel().getIdLong() == guild.getBotChannel())
            guild.setBotChannel(0);
        guild.getBlacklistedChannels().forEach(channel -> {
            if (GroovyBot.getInstance().getShardManager().getTextChannelById((long) channel) == null)
                guild.unBlacklistChannel((long) channel);
        });
    }
}
