package io.groovybot.bot.listeners;

import io.groovybot.bot.core.cache.Cache;
import io.groovybot.bot.core.entity.Guild;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class BlacklistWatcher {

    private final Cache<Guild> guildCache;

    @SubscribeEvent
    private void onTextChannelDelete(TextChannelDeleteEvent event) {
        if (event.getGuild() == null) return;
        final Guild guild = guildCache.get(event.getGuild().getIdLong()); // TODO: CHECK NULLPOINTER FIX
        final long channelId = event.getChannel().getIdLong();
        if (guild.isChannelBlacklisted(channelId))
            guild.unBlacklistChannel(channelId);
        if (event.getChannel().equals(guild.getBotChannel()))
            guild.setBotChannel(null);
    }
}
