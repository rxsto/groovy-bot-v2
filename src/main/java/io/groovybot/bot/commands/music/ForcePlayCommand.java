package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SemiInChannelCommand;

public class ForcePlayCommand extends SemiInChannelCommand {

    public ForcePlayCommand() {
        super(new String[]{"forceplay", "playskip"}, CommandCategory.MUSIC, Permissions.djMode(), "Like play but adds the song to the top and skips", "<song>");
    }

    @Override
    protected Result executeCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0)
            return sendHelp();

        player.setPreviousTrack(player.getPlayer().getPlayingTrack());

        player.queueSongs(event, true, false);
        return null;
    }
}
