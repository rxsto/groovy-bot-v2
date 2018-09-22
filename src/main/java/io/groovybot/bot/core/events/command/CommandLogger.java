package io.groovybot.bot.core.events.command;

import io.groovybot.bot.util.SafeMessage;
import lombok.extern.log4j.Log4j;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.awt.*;

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
                .setTitle("Error! An internal error occured!")
                .setDescription(String.format(":no_entry_sign: We're sorry, but an internal error occured\n```%s```", failEvent.getThrowable().getClass().getCanonicalName() + ": " + failEvent.getThrowable().getMessage()))
                .setColor(new Color(219, 18, 0));
        SafeMessage.sendMessage(failEvent.getChannel(), builder);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onPermissionViolations(NoPermissionEvent noPermissionEvent) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Error! No Permisssions!")
                .setDescription(":no_entry_sign: You are not allowed to execute this command!")
                .setColor(new Color(219, 18, 0));
        SafeMessage.sendMessage(noPermissionEvent.getChannel(), builder);
    }
}
