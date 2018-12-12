package io.groovybot.bot.core.events.command;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandEvent;

public class NoPermissionEvent extends CommandExecutedEvent {

    public NoPermissionEvent(CommandEvent event, Command command) {
        super(event, command);
    }
}
