package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.*;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.Colors;
import io.groovybot.bot.util.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;

public class ShardCommand extends Command {

    public ShardCommand() {
        super(new String[]{"shard", "shards"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you some information about your current shard", "");
        registerSubCommand(new ListCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.DARK_BUT_NOT_BLACK);
        builder.setDescription("<:online:449207830105554964> " + String.format(event.translate("command.shard.description"), event.getJDA().getShardInfo().getShardId() + 1, event.getJDA().getPing()));
        return send(builder);
    }

    private class ListCommand extends SubCommand {

        public ListCommand() {
            super(new String[]{"list", "all"}, Permissions.everyone(), "Lists all shards with information", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            String message = FormatUtil.parseShardsMessage(event);
            return send(message);
        }
    }
}