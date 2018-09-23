package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class ShardCommand extends Command {
    public ShardCommand() {
        super(new String[] {"shard", "shards"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you some information about your current shard", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return null;
    }
}
