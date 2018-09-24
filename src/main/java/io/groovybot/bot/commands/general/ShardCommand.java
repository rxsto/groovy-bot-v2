package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.entity.EntityProvider;
import net.dv8tion.jda.core.EmbedBuilder;

public class ShardCommand extends Command {
    public ShardCommand() {
        super(new String[] {"shard", "shards"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you some information about your current shard", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(":white_check_mark: " + String.format(event.translate("command.shard.description"), event.getJDA().getShardInfo().getShardId(), event.getJDA().getPing()));
        return null;
    }
}
