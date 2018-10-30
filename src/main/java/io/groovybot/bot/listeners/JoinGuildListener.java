package io.groovybot.bot.listeners;

import io.groovybot.bot.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.*;

public class JoinGuildListener {

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onGuildJoin(GuildJoinEvent event) {
        joinMessage(event.getGuild());
    }

    private void joinMessage(Guild guild) {
        List channels = guild.getTextChannels();

        Map<String, TextChannel> sortedChannels = new HashMap<>();
        Set<TextChannel> preferredChannels = new HashSet<>();

        for (Object channel : channels) {
            TextChannel textChannel = ((TextChannel) channel);
            sortedChannels.put(textChannel.getName(), textChannel);
        }

        sortedChannels.forEach((name, channel) -> {
            if (name.contains("music") || name.contains("bot") || name.contains("command") || name.contains("talk") || name.contains("chat") || name.contains("general"))
                preferredChannels.add(channel);
        });

        boolean found = false;

        for (Object channel : preferredChannels)
            if (((TextChannel) channel).canTalk()) {
                EmbedUtil.sendMessageBlocking(((TextChannel) channel), EmbedUtil.welcome(guild));
                found = true;
                break;
            }

        if (!found)
            for (Object channel : channels)
                if (((TextChannel) channel).canTalk()) {
                    EmbedUtil.sendMessageBlocking(((TextChannel) channel), EmbedUtil.welcome(guild));
                    break;
                }
    }
}
