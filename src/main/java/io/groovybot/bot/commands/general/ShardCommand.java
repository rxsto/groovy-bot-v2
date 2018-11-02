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
        registerSubCommand(new RestartCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (event.getMessage().getContentRaw().contains("-list")) return send(FormatUtil.parseShardsMessage(event));

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.DARK_BUT_NOT_BLACK);
        builder.setDescription(String.format("<:online:449207830105554964> **Shard %s online » %sms**", event.getJDA().getShardInfo().getShardId() + 1, event.getJDA().getPing()));
        return send(builder);
    }

    private class RestartCommand extends SubCommand {

        public RestartCommand() {
            super(new String[]{"restart", "r"}, Permissions.ownerOnly(), "Allows you to restart specific shards", "all/<shardId>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length == 0) return sendHelp();

            boolean failed = false;

            List<Integer> shards = new ArrayList<>();

            for (String arg : args) {
                try {
                    int testForInteger = Integer.parseInt(arg);
                    shards.add(testForInteger);
                } catch (NumberFormatException e) {
                    failed = true;
                    break;
                }
            }

            if (failed)
                return send(error(event.translate("phrases.invalidnumber.title"), event.translate("phrases.invalidnumber.description")));

            if (shards.size() == 1) {
                send(info(event.translate("command.shard.restart.success.title"), String.format(event.translate("command.shard.restart.success.title"), shards.get(0))));
                event.getBot().getShardManager().restart(shards.get(0) - 1);
            } else {
                send(info(event.translate("command.shard.restart.all.success.title"), event.translate("command.shard.restart.all.success.title")));
                shards.forEach(shard -> event.getBot().getShardManager().restart(shard - 1));
            }

            return null;
        }
    }
}
