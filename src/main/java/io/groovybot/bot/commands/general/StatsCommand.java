package io.groovybot.bot.commands.general;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;

import java.text.DecimalFormat;

public class StatsCommand extends Command {
    public StatsCommand() {
        super(new String[]{"stats"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you Groovy's current stats", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.DARK_BUT_NOT_BLACK);
        builder.setTitle(":chart_with_upwards_trend: " + event.translate("command.stats.title"));
        builder.setThumbnail(event.getGuild().getSelfMember().getUser().getAvatarUrl());

        DecimalFormat decimalFormat = new DecimalFormat("###.##");

        StringBuilder message = new StringBuilder();
        message.append(String.format("\n" + event.translate("command.stats.text.playing") + "\n", GroovyBot.getInstance().getMusicPlayerManager().getPlayerStorage().size()));
        message.append(String.format(event.translate("command.stats.text.servers") + "\n", event.getJDA().getGuilds().size()));
        message.append(String.format(event.translate("command.stats.text.members") + "\n", event.getJDA().getUsers().size()));
        message.append(String.format(event.translate("command.stats.text.latency") + "\n", event.getJDA().getPing()));
        message.append(String.format(event.translate("command.stats.text.shards") + "\n", event.getJDA().getShardInfo().getShardId() + 1, event.getJDA().getShardInfo().getShardTotal()));
        message.append(String.format(event.translate("command.stats.text.memory") + "\n", decimalFormat.format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()), decimalFormat.format(Runtime.getRuntime().totalMemory())));

        builder.setDescription(message);

        return send(builder);
    }
}
