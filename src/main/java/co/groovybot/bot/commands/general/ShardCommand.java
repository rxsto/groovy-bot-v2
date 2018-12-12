package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;

public class ShardCommand extends Command {

    public ShardCommand() {
        super(new String[]{"shard", "shards"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you information about the shard Groovy is running on", "[-list]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.DARK_BUT_NOT_BLACK);
        builder.setDescription(String.format("<:online:449207830105554964> Shard **%s** online » **%s**ms", event.getJDA().getShardInfo().getShardId() + 1, event.getJDA().getPing()));
        return send(builder);
    }
}
