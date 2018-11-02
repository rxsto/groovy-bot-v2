package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.audio.Scheduler;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class LoopCommand extends SameChannelCommand {

    public LoopCommand() {
        super(new String[]{"loop", "lp"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you toggle the loop-mode", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        Scheduler scheduler = player.getScheduler();

        if (scheduler.isLoopqueue())
            return send(error(event.translate("command.control.disable.loopqueue.title"), event.translate("command.control.disable.loopqueue.description")));

        if (scheduler.isLoop()) {
            scheduler.setLoop(false);
            return send(success(event.translate("command.loop.disabled.title"), event.translate("command.loop.disabled.description")));
        }

        scheduler.setLoop(true);
        return send(success(event.translate("command.loop.enabled.title"), event.translate("command.loop.enabled.description")));
    }
}
