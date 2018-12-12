package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;

public class ClearCommand extends SameChannelCommand {

    public ClearCommand() {
        super(new String[]{"clear", "purge"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you clear the queue", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        player.purgeQueue();
        return send(success(event.translate("command.clear.cleared.title"), event.translate("command.clear.cleared.description")));
    }
}
