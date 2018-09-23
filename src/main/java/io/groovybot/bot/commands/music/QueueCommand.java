package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class QueueCommand extends Command {
    public QueueCommand() {
        super(new String[] {"queue", "q"}, CommandCategory.MUSIC, Permissions.everyone(), "Shows you each song inside the queue", "[site]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return null;
    }
}
