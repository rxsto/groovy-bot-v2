package io.groovybot.bot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.Colors;
import io.groovybot.bot.util.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;

public class NowPlayingCommand extends Command {
    public NowPlayingCommand() {
        super(new String[]{"now", "np", "nowplaying"}, CommandCategory.MUSIC, Permissions.everyone(), "Shows you all information about the current track", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        MusicPlayer player = GroovyBot.getInstance().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.DARK_BUT_NOT_BLACK);
        AudioTrack playingTrack = player.getPlayer().getPlayingTrack();
        builder.setTitle(String.format(":notes: %s", playingTrack.getInfo().title), playingTrack.getInfo().uri);
        builder.setThumbnail(FormatUtil.getThumbnail(playingTrack));
        builder.addField(event.translate("command.now.title"), String.format("**%s:** %s - **%s:** [%s/%s]", event.translate("phrases.text.author"), playingTrack.getInfo().author, event.translate("phrases.text.progress"), FormatUtil.formatTimestamp(playingTrack.getPosition()), FormatUtil.formatTimestamp(playingTrack.getDuration())), false);
        return send(builder);
    }
}
