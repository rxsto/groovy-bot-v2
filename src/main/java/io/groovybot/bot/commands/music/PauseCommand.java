package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class PauseCommand extends SameChannelCommand {
    public PauseCommand() {
        super(new String[]{"pause"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you pause the bot", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        if (player.isPaused())
            return send(error(event.translate("command.pause.already.title"), event.translate("command.pause.already.description")));

        player.pause();
        return send(success(event.translate("command.pause.title"), event.translate("command.pause.description")));
    }
}
