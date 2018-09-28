package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;
import io.groovybot.bot.util.FormatUtil;
import net.dv8tion.jda.core.utils.Helpers;

public class SeekCommand extends SameChannelCommand {

    public SeekCommand() {
        super(new String[] {"seek"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you seek to specific positions", "[-]<seconds>");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0)
            return sendHelp();
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        String input = args[0].replace("-", "");
        if (!Helpers.isNumeric(input)) {
            return send(error(event.translate("phrases.invalidnumber.title"), event.translate("phrases.invalidnumber.description")));
        }
        if (input.length() > ("" + player.getPlayer().getPlayingTrack().getDuration() / 1000).length()) {
            player.seekTo(player.getPlayer().getPlayingTrack().getDuration());
            return send(error(event.translate("command.seek.skipped.title"), event.translate("command.seek.skipped.description")));
        }
        int seconds = Integer.parseInt(input) * 1000;
        if (args[0].startsWith("-"))
            seconds = ~seconds;
        long position = player.getPlayer().getTrackPosition() + seconds;
        player.seekTo(position);
        if (position > player.getPlayer().getPlayingTrack().getDuration())
            return send(error(event.translate("command.seek.skipped.title"), event.translate("command.seek.skipped.description")));
        return send(success(event.translate("command.seek.success.title"), String.format(event.translate("command.seek.success.description"), FormatUtil.formatTimestamp(position))));
    }
}
