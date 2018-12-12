package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;

public class ResetCommand extends SameChannelCommand {

    public ResetCommand() {
        super(new String[]{"reset", "restart"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you reset the playing track's progress", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        player.seekTo(0);
        return send(success(event.translate("command.reset.title"), event.translate("command.reset.description")));
    }
}
