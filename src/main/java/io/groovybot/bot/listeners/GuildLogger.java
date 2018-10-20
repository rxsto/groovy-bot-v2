package io.groovybot.bot.listeners;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import java.time.Instant;
import java.util.*;

public class GuildLogger {

    private final WebhookClient client;

    public GuildLogger() {
        client = new WebhookClientBuilder(GroovyBot.getInstance().getConfig().getJSONObject("webhooks").getString("guilds")).build();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onGuildJoin(GuildJoinEvent event) {
        sendMessage(event.getGuild(), true, event);
        joinMessage(event.getGuild());
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onGuildLeave(GuildLeaveEvent event) {
        sendMessage(event.getGuild(), false, event);
    }

    private void sendMessage(Guild guild, boolean prefix, Event event) {
        WebhookMessageBuilder out = new WebhookMessageBuilder();
        out.addEmbeds(
                EmbedUtil.join(String.format("%s guild %s (%s)", prefix ? "Joined" : "Left", guild.getName(), guild.getId()), String.format("**Owner:** %s#%s\n**Members:** %s\n**Shard:** %s", guild.getOwner().getUser().getName(), guild.getOwner().getUser().getDiscriminator(), guild.getMembers().size(), event.getJDA().getShardInfo().getShardId() + 1), prefix)
                        .setTimestamp(Instant.now())
                        .setThumbnail(guild.getIconUrl())
                        .build()
        );
        client.send(out.build());
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
            if (name.contains("music"))
                preferredChannels.add(channel);
            if (name.contains("bot"))
                preferredChannels.add(channel);
            if (name.contains("command"))
                preferredChannels.add(channel);
            if (name.contains("talk"))
                preferredChannels.add(channel);
            if (name.contains("chat"))
                preferredChannels.add(channel);
            if (name.contains("general"))
                preferredChannels.add(channel);
        });

        boolean found = false;

        for (Object channel : preferredChannels) {
            if (((TextChannel) channel).canTalk()) {
                EmbedUtil.sendMessageBlocking(((TextChannel) channel), EmbedUtil.welcome(guild));
                found = true;
                break;
            }
        }

        if (found)
            return;

        for (Object channel : channels) {
            if (((TextChannel) channel).canTalk()) {
                EmbedUtil.sendMessageBlocking(((TextChannel) channel), EmbedUtil.welcome(guild));
                break;
            }
        }
    }
}
