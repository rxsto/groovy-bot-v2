package co.groovybot.bot.core.events.command;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandEvent;
import lombok.Getter;

public class CommandExecutedEvent extends CommandEvent {

    @Getter
    private final Command command;

    public CommandExecutedEvent(CommandEvent event, Command command) {
        super(event, event.getBot(), event.getArgs(), event.getInvocation());
        this.command = command;
    }
}
