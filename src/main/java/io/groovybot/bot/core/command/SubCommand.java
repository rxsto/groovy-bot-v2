package io.groovybot.bot.core.command;

import io.groovybot.bot.core.command.permission.Permissions;

public abstract class SubCommand extends Command {

    public SubCommand(String[] aliases,  Permissions permissions, String description, String usage) {
        super(aliases, null, permissions, description, usage);
    }

}
