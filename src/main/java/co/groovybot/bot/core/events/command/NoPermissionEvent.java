package co.groovybot.bot.core.events.command;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandEvent;

public class NoPermissionEvent extends CommandExecutedEvent {

    public NoPermissionEvent(CommandEvent event, Command command) {
        super(event, command);
    }
}
