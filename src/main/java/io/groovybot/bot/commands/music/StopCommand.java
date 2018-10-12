package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class StopCommand extends SameChannelCommand {

    public StopCommand() {
        super(new String[]{"stop"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you stop the bot", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        player.setPreviousTrack(player.getPlayer().getPlayingTrack());
        player.stop();
        return send(success(event.translate("command.stop.stopped.title"), event.translate("command.stop.stopped.description")));
    }
}
