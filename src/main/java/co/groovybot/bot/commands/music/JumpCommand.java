package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;
import co.groovybot.bot.util.FormatUtil;
import net.dv8tion.jda.core.utils.Helpers;

public class JumpCommand extends SameChannelCommand {

    public JumpCommand() {
        super(new String[]{"jump", "jumpto"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you seek forwards or backwards", "[-]<seconds>");
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
            return send(error(event.translate("command.jumpto.skipped.title"), event.translate("command.jumpto.skipped.description")));
        }
        int seconds = Integer.parseInt(input) * 1000;
        if (args[0].startsWith("-"))
            seconds = ~seconds;
        long position = player.getPlayer().getTrackPosition() + seconds;
        player.seekTo(position);
        if (position > player.getPlayer().getPlayingTrack().getDuration())
            return send(error(event.translate("command.jumpto.skipped.title"), event.translate("command.jumpto.skipped.description")));
        return send(success(event.translate("command.jumpto.success.title"), String.format(event.translate("command.jumpto.success.description"), FormatUtil.formatTimestamp(position))));
    }
}
