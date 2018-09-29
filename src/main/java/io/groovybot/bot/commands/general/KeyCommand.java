package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.*;
import io.groovybot.bot.core.command.permission.Permissions;

public class KeyCommand extends Command {

    public KeyCommand() {
        super(new String[] {"key", "redeem"}, CommandCategory.GENERAL, Permissions.everyone(), "Allows you to redeem keys", "");
        registerSubCommand(new CreateCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return null;
    }

    private class CreateCommand extends SubCommand {

        public CreateCommand() {
            super(new String[] {"create", "generate"}, Permissions.ownerOnly(), "Allows you to create keys", "<type>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            return null;
        }
    }
}
