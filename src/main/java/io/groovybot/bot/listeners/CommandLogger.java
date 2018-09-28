package io.groovybot.bot.listeners;

import io.groovybot.bot.core.events.command.CommandExecutedEvent;
import io.groovybot.bot.core.events.command.CommandFailEvent;
import io.groovybot.bot.core.events.command.NoPermissionEvent;
import io.groovybot.bot.util.Colors;
import io.groovybot.bot.util.EmbedUtil;
import io.groovybot.bot.util.SafeMessage;
import lombok.extern.log4j.Log4j;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@Log4j
public class CommandLogger {

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onCommandExecution(CommandExecutedEvent executedEvent) {
        log.debug(String.format("[Command] Command %s got executed by %s on guild %s(%d)", executedEvent.getCommand().getClass().getCanonicalName(), executedEvent.getAuthor().getName(), executedEvent.getGuild().getName(), executedEvent.getGuild().getIdLong()));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onCommandFail(CommandFailEvent failEvent) {
        log.error(String.format("[Command] Command %s threw an error %s on guild %s(%d)", failEvent.getCommand().getClass().getCanonicalName(), failEvent.getAuthor().getName(), failEvent.getGuild().getName(), failEvent.getGuild().getIdLong()), failEvent.getThrowable());
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(":no_entry_sign: " + failEvent.translate("phrases.error.internal"))
                .setDescription(String.format("We're sorry, but an internal error occured\n```%s```", failEvent.getThrowable().getClass().getCanonicalName() + ": " + failEvent.getThrowable().getMessage()))
                .setColor(Colors.DARK_BUT_NOT_BLACK);
        SafeMessage.sendMessage(failEvent.getChannel(), builder);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onPermissionViolations(NoPermissionEvent noPermissionEvent) {
        String permission = noPermissionEvent.getCommand().getPermissions().getIdentifier();
        EmbedBuilder builder = EmbedUtil.error(noPermissionEvent.translate("phrases.nopermission.title"), String.format(noPermissionEvent.translate("phrases.nopermission.%s.description"), permission));
        SafeMessage.sendMessage(noPermissionEvent.getChannel(), builder);
    }
}
