package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;

public class StatsCommand extends Command {
    public StatsCommand() {
        super(new String[]{"stats"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you Groovy's current stats", "");
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + ("");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.DARK_BUT_NOT_BLACK);
        builder.setTitle(":chart_with_upwards_trend: " + event.translate("command.stats.title"));
        builder.setThumbnail(event.getGuild().getSelfMember().getUser().getAvatarUrl());

        StringBuilder message = new StringBuilder();
        message.append(String.format("\n\n" + event.translate("command.stats.text.playing") + "\n", event.getGroovyBot().getMusicPlayerManager().getPlayingServers()));
        message.append(String.format(event.translate("command.stats.text.servers") + "\n", event.getGroovyBot().getShardManager().getGuilds().size()));
        message.append(String.format(event.translate("command.stats.text.members") + "\n", event.getGroovyBot().getShardManager().getUsers().size()));
        message.append(String.format(event.translate("command.stats.text.latency") + "\n", event.getJDA().getPing()));
        message.append(String.format(event.translate("command.stats.text.shards") + "\n", event.getJDA().getShardInfo().getShardId() + 1, event.getJDA().getShardInfo().getShardTotal()));
        message.append(String.format(event.translate("command.stats.text.memory") + "\n", humanReadableByteCount(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()), humanReadableByteCount(Runtime.getRuntime().totalMemory())));
        message.append(String.format(event.translate("command.stats.text.threads") + "\n", Thread.getAllStackTraces().size()));

        builder.setDescription(message);
        sendMessageBlocking(event.getChannel(), builder);
        return null;
    }
}
