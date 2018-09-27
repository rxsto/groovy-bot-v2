package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class ResumeCommand extends SameChannelCommand {
    public ResumeCommand() {
        super(new String[] {"resume"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you resume the bot", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        if (!player.isPaused())
            return send(error(event.translate("command.resume.already.title"), event.translate("command.resume.already.description")));

        player.resume();
        return send(success(event.translate("command.resume.title"), event.translate("command.resume.description")));
    }
}
