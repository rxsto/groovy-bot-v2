package io.groovybot.bot.core.events.command;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandEvent;
import lombok.Getter;

public class CommandExecutedEvent extends CommandEvent {

    @Getter
    private final Command command;

    public CommandExecutedEvent(CommandEvent event, Command command) {
        super(event, event.getGroovyBot(), event.getArgs(), event.getInvocation());
        this.command = command;
    }
}
