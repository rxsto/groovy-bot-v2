/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

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
        builder.addField(event.translate("command.stats.text.playing"), String.format("`%s` %s", event.getBot().getMusicPlayerManager().getPlayingServers(), event.translate("phrases.text.players")), true);
        builder.addField(event.translate("command.stats.text.servers"), String.format("`%s` %s", event.getBot().getShardManager().getGuilds().size(), event.translate("phrases.text.guilds")), true);
        builder.addField(event.translate("command.stats.text.members"), String.format("`%s` %s", event.getBot().getShardManager().getUsers().size(), event.translate("phrases.text.users")), true);
        builder.addField(event.translate("command.stats.text.latency"), String.format("`%s` ms", event.getJDA().getPing()), true);
        builder.addField(event.translate("command.stats.text.shards"), String.format("`%s` %s", event.getBot().getShardManager().getShardsTotal(), event.translate("phrases.text.shards")), true);
        builder.addField(event.translate("command.stats.text.cpu"), String.format("`%s`", Math.round(operatingSystemMXBean.getSystemCpuLoad() * 100) + "%"), true);
        builder.addField(event.translate("command.stats.text.memory"), String.format("`%s`", FormatUtil.humanReadableByteCount(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())), true);
        builder.addField(event.translate("command.stats.text.threads"), String.format("`%s` %s", Thread.getAllStackTraces().size(), event.translate("phrases.text.threads")), true);
        builder.addField(event.translate("command.stats.text.uptime"), String.format("`%s`", FormatUtil.parseUptime(System.currentTimeMillis() - event.getBot().getStartupTime())), true);
        return send(builder);
    }
}
