package io.groovybot.bot.listeners;

import io.groovybot.bot.core.events.command.CommandExecutedEvent;
import io.groovybot.bot.core.events.command.CommandFailEvent;
import io.groovybot.bot.core.events.command.NoPermissionEvent;
import io.groovybot.bot.util.Colors;
import io.groovybot.bot.util.EmbedUtil;
import io.groovybot.bot.util.SafeMessage;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@Log4j2
public class CommandLogger {

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onCommandExecution(CommandExecutedEvent executedEvent) {
        log.debug(String.format("[Command] %s - %s#%s | %s (%s)", executedEvent.getCommand().getName(), executedEvent.getAuthor().getName(), executedEvent.getAuthor().getDiscriminator(), executedEvent.getGuild().getName(), executedEvent.getGuild().getIdLong()));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onCommandFail(CommandFailEvent failEvent) {
        log.error(String.format("[Command] %s - %s#%s | %s (%s)", failEvent.getCommand().getName(), failEvent.getAuthor().getName(), failEvent.getAuthor().getDiscriminator(), failEvent.getGuild().getName(), failEvent.getGuild().getIdLong()), failEvent.getThrowable());
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("❌ " + failEvent.translate("phrases.error.internal"))
                .setDescription(String.format("We're sorry, but an internal error occured\n```%s```", failEvent.getThrowable().getClass().getCanonicalName() + ": " + failEvent.getThrowable().getMessage()))
                .setColor(Colors.DARK_BUT_NOT_BLACK);
        SafeMessage.sendMessage(failEvent.getChannel(), builder);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onPermissionViolations(NoPermissionEvent noPermissionEvent) {
        String permission = noPermissionEvent.getCommand().getPermissions().getIdentifier();
        EmbedBuilder builder = EmbedUtil.error(noPermissionEvent.translate("phrases.nopermission.title"), noPermissionEvent.translate(String.format("phrases.nopermission.%s", permission)));
        SafeMessage.sendMessage(noPermissionEvent.getChannel(), builder);
    }
}
