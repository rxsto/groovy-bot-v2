package io.groovybot.bot.listeners;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.events.command.CommandExecutedEvent;
import io.groovybot.bot.core.events.command.CommandFailEvent;
import io.groovybot.bot.util.FormatUtil;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import java.util.Objects;

@Log4j2
public class Logger {

    private final WebhookClient guildLogger;
    private final WebhookClient userLogger;
    private final WebhookClient errorLogger;

    public Logger() {
        guildLogger = new WebhookClientBuilder(GroovyBot.getInstance().getConfig().getJSONObject("webhooks").getString("guild_logger")).build();
        userLogger = new WebhookClientBuilder(GroovyBot.getInstance().getConfig().getJSONObject("webhooks").getString("user_logger")).build();
        errorLogger = new WebhookClientBuilder(GroovyBot.getInstance().getConfig().getJSONObject("webhooks").getString("error_logger")).build();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onUserJoin(GuildMemberJoinEvent event) {
        if (!event.getGuild().getId().equals("403882830225997825"))
            return;
        sendMessage("MEMBERJOIN", event, userLogger);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onUserLeave(GuildMemberLeaveEvent event) {
        if (!event.getGuild().getId().equals("403882830225997825"))
            return;
        sendMessage("MEMBERLEAVE", event, userLogger);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onGuildJoin(GuildJoinEvent event) {
        sendMessage("GUILDJOIN", event, guildLogger);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onGuildLeave(GuildLeaveEvent event) {
        sendMessage("GUILDLEAVE", event, guildLogger);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onCommandExecution(CommandExecutedEvent executedEvent) {

    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onCommandFail(CommandFailEvent event) {
        sendMessage("ERROR", event, errorLogger);
    }

    private void sendMessage(String type, Event event, WebhookClient client) {
        WebhookMessageBuilder webhookMessageBuilder = new WebhookMessageBuilder();
        webhookMessageBuilder.addEmbeds(Objects.requireNonNull(FormatUtil.formatWebhookMessage(type, event)).build());
        client.send(webhookMessageBuilder.build());
    }
}
