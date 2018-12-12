package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.util.Colors;
import co.groovybot.bot.util.FormatUtil;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.EmbedBuilder;

import java.lang.management.ManagementFactory;

public class StatsCommand extends Command {
    public StatsCommand() {
        super(new String[]{"stats"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you statistics about Groovy", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Colors.DARK_BUT_NOT_BLACK);
        builder.setTitle(":chart_with_upwards_trend: " + event.translate("command.stats.title"));
        builder.addField(event.translate("command.stats.text.playing"), String.format("**%s** %s", event.getBot().getMusicPlayerManager().getPlayingServers(), event.translate("phrases.text.guilds")), true);
        builder.addField(event.translate("command.stats.text.servers"), String.format("**%s** %s", event.getBot().getShardManager().getGuilds().size(), event.translate("phrases.text.guilds")), true);
        builder.addField(event.translate("command.stats.text.members"), String.format("**%s** %s", event.getBot().getShardManager().getUsers().size(), event.translate("phrases.text.users")), true);
        builder.addField(event.translate("command.stats.text.latency"), String.format("**%s**ms", event.getJDA().getPing()), true);
        builder.addField(event.translate("command.stats.text.shards"), String.format("**%s**/**%s** %s", event.getJDA().getShardInfo().getShardId() + 1, event.getJDA().getShardInfo().getShardTotal(), event.translate("phrases.text.shards")), true);
        builder.addField(event.translate("command.stats.text.cpu"), String.format("**%s**", Math.round(operatingSystemMXBean.getSystemCpuLoad() * 100) + "%"), true);
        builder.addField(event.translate("command.stats.text.memory"), String.format("**%s**", FormatUtil.humanReadableByteCount(operatingSystemMXBean.getTotalPhysicalMemorySize() - operatingSystemMXBean.getFreePhysicalMemorySize())), true);
        builder.addField(event.translate("command.stats.text.threads"), String.format("**%s** %s", Thread.getAllStackTraces().size(), event.translate("phrases.text.threads")), true);
        builder.addField(event.translate("command.stats.text.uptime"), String.format("**%s**", FormatUtil.parseUptime(System.currentTimeMillis() - event.getBot().getStartupTime())), true);

        return send(builder);
    }
}
