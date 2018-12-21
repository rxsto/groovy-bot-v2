package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.audio.Scheduler;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;

public class ShuffleCommand extends SameChannelCommand {

    public ShuffleCommand() {
        super(new String[]{"shuffle", "sh"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you toggle the shuffle-mode", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        Scheduler scheduler = player.getScheduler();

        if (scheduler.isShuffle()) {
            scheduler.setShuffle(false);
            return send(success(event.translate("command.shuffle.disabled.title"), event.translate("command.shuffle.disabled.description")));
        }

        scheduler.setShuffle(true);
        return send(success(event.translate("command.shuffle.enabled.title"), event.translate("command.shuffle.enabled.description")));
    }
}
