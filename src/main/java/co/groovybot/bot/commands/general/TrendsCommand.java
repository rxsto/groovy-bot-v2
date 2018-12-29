/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergeij Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
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

import co.groovybot.bot.core.audio.playlists.Rank;
import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.util.Colors;
import co.groovybot.bot.util.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;

public class TrendsCommand extends Command {
    public TrendsCommand() {
        super(new String[]{"trends", "trend", "tr"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you the latest trends", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(event.translate("command.trends.playlist.top.title"))
                .setColor(Colors.DARK_BUT_NOT_BLACK);

        event.getBot().getPlaylistManager().getTopPlaylists().forEach((rank, playlist) -> {
            String position;
            switch (rank) {
                case 1:
                    position = Rank.ONE.getName();
                    break;
                case 2:
                    position = Rank.TWO.getName();
                    break;
                case 3:
                    position = Rank.THREE.getName();
                    break;
                case 4:
                    position = Rank.FOUR.getName();
                    break;
                case 5:
                    position = Rank.FIVE.getName();
                    break;
                default:
                    position = "None";
                    break;
            }

            if (event.getBot().getShardManager().getUserById(playlist.getAuthorId()) == null) return;

            builder.addField(String.format("%s **%s** (%s)", position, playlist.getName(), FormatUtil.formatUserName(event.getBot().getShardManager().getUserById(playlist.getAuthorId()))), String.format(" - Includes **%s** songs\n - Loaded **%s** times\n - ID: `%s`", playlist.getSongs().size(), playlist.getCount(), playlist.getId()), false);
        });
        return send(builder);
    }
}
