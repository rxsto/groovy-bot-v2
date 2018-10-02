package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.audio.Scheduler;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class ShuffleCommand extends SameChannelCommand {

    public ShuffleCommand() {
        super(new String[]{"shuffle", "sh"}, CommandCategory.MUSIC, Permissions.tierTwo(), "Lets you activate shuffling", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        final Scheduler scheduler = player.getScheduler();
        if (scheduler.isShuffle()) {
            scheduler.setShuffle(false);
            return send(success(event.translate("command.shuffle.disabled.title"), event.translate("command.shuffle.disabled.description")));
        }
        scheduler.setShuffle(true);
        return send(success(event.translate("command.shuffle.enabled.title"), event.translate("command.shuffle.enabled.description")));
    }
}
