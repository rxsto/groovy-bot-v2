package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.audio.playlists.Rank;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.Colors;
import io.groovybot.bot.util.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;

public class TrendsCommand extends Command {
    public TrendsCommand() {
        super(new String[]{"trends", "trend", "tr"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you the latest trends", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder()
                .setDescription(event.translate("command.trends.playlist.top.title"))
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
