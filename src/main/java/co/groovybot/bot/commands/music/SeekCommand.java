package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;
import co.groovybot.bot.util.FormatUtil;
import lombok.extern.log4j.Log4j2;

import java.text.ParseException;

@Log4j2
public class SeekCommand extends SameChannelCommand {

    public SeekCommand() {
        super(new String[]{"seek", "seekto"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets you seek to a specific position", "[HH]:[mm]:<ss>");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0)
            return sendHelp();
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        long position;
        try {
            position = FormatUtil.convertTimestamp(event.getArguments());
        } catch (ParseException e) {
            return send(error(event.translate("command.seek.invalidinput.title"), event.translate("command.seek.invalidinput.description")));
        }
        player.seekTo(position);
        return send(success(event.translate("command.seek.success.title"), String.format(event.translate("command.seek.success.description"), FormatUtil.formatTimestamp(position))));
    }
}
