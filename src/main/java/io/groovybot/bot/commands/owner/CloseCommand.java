package io.groovybot.bot.commands.owner;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.SafeMessage;

public class CloseCommand extends Command {

    public CloseCommand() {
        super(new String[]{"close", "stopbot", "botstop"}, CommandCategory.DEVELOPER, Permissions.ownerOnly(), "Stops the bot", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        SafeMessage.sendMessageBlocking(event.getChannel(), success("Stopped bot!", "Successfully stopped bot!"));
        System.exit(1);
        return null;
    }
}
