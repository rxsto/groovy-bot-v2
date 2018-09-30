package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class PlayTopCommand extends SameChannelCommand {

    public PlayTopCommand() {
        super(new String[]{"playtop", "pt", "addtop", "at"}, CommandCategory.MUSIC, Permissions.djMode(), "", "<song/url>");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0)
            return sendHelp();
        player.queueSongs(event, false, true);
        return null;
    }
}
