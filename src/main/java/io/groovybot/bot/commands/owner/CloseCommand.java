package io.groovybot.bot.commands.owner;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.SafeMessage;

public class CloseCommand extends Command {

    public CloseCommand() {
        super(new String[] {"stopbot", "botstop"}, CommandCategory.DEVELOPER, Permissions.ownerOnly(), "Stops the whole bot", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        SafeMessage.sendMessageBlocking(event.getChannel(), error("Stooooopping bot ...", "Stooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooping booooooooooooo .... . . ."));
        System.exit(1);
        return null;
    }
}
