package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SemiInChannelCommand;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PlayCommand extends SemiInChannelCommand {

    public PlayCommand() {
        super(new String[]{"play", "p", "add"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets you play any music you want", "[-soundcloud] [-forceplay] [-playtop] <song/url>");
    }

    @Override
    public Result executeCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0) {
            if (player.isPaused()) {
                player.resume();
                return send(success(event.translate("command.resume.title"), event.translate("command.resume.description")));
            }
            return sendHelp();
        }
        player.queueSongs(event);
        return null;
    }
}
