package co.groovybot.bot.commands.owner;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.util.SafeMessage;

public class CloseCommand extends Command {

    public CloseCommand() {
        super(new String[]{"close", "stopbot", "botstop"}, CommandCategory.DEVELOPER, Permissions.ownerOnly(), "Lets you close Groovy", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        SafeMessage.sendMessageBlocking(event.getChannel(), success("Stopped bot!", "Successfully stopped bot!"));
        System.exit(0);
        return null;
    }
}
