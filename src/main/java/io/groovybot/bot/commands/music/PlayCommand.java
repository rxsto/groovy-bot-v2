package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SemiInChannelCommand;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PlayCommand extends SemiInChannelCommand {

    public PlayCommand() {
        super(new String[]{"play", "p", "add"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets you play any music you want", "<song/url>");
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
        player.queueSongs(event, false, false);
        return null;
    }

}
