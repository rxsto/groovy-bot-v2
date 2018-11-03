package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.*;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.Colors;
import io.groovybot.bot.util.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;

public class ShardCommand extends Command {

    public ShardCommand() {
        super(new String[]{"shard", "shards"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you information about the shard Groovy is running on", "[-list]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.DARK_BUT_NOT_BLACK);
        builder.setDescription(String.format("<:online:449207830105554964> **Shard %s online » %sms**", event.getJDA().getShardInfo().getShardId() + 1, event.getJDA().getPing()));
        return send(builder);
    }
}
