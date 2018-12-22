package co.groovybot.bot.commands.music;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.util.Colors;
import co.groovybot.bot.util.FormatUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;

public class NowPlayingCommand extends Command {
    public NowPlayingCommand() {
        super(new String[]{"now", "np", "n", "nowplaying"}, CommandCategory.MUSIC, Permissions.everyone(), "Shows you all information about the current playing track", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        MusicPlayer player = GroovyBot.getInstance().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());

        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.DARK_BUT_NOT_BLACK);

        AudioTrack playingTrack = player.getPlayer().getPlayingTrack();
        final long trackPosition = player.getPlayer().getTrackPosition();

        builder.setDescription(String.format("\uD83C\uDFA4 [%s](%s) (%s)", playingTrack.getInfo().title, playingTrack.getInfo().uri, playingTrack.getInfo().author));
        builder.setFooter(String.format("%s [%s]", FormatUtil.formatProgressBar(trackPosition, playingTrack.getDuration()), playingTrack.getInfo().isStream ? event.translate("phrases.text.stream") : String.format("%s/%s", FormatUtil.formatTimestamp(trackPosition), FormatUtil.formatTimestamp(playingTrack.getDuration()))), null);
        return send(builder);
    }
}
