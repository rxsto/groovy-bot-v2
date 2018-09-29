package io.groovybot.bot.listeners;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import java.time.Instant;

public class GuildLogger {

    private final WebhookClient client;

    public GuildLogger() {
        client = new WebhookClientBuilder(GroovyBot.getInstance().getConfig().getJSONObject("webhooks").getString("guilds")).build();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onGuildJoin(GuildJoinEvent event) {
        sendMessage(event.getGuild(), true, event);

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
}
