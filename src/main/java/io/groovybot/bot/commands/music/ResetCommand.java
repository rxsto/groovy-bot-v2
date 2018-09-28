package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class ResetCommand extends SameChannelCommand {

    public ResetCommand() {
        super(new String[]{"reset", "restart"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets you reset the current progress", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        player.seekTo(0);
        return send(success(event.translate("command.reset.title"), event.translate("command.reset.description")));
    }
}
