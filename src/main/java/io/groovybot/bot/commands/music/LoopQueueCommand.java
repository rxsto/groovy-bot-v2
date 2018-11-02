package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.audio.Scheduler;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class LoopQueueCommand extends SameChannelCommand {

    public LoopQueueCommand() {
        super(new String[]{"loopqueue", "lq", "looprepeat", "lr"}, CommandCategory.MUSIC, Permissions.tierOne(), "Lets you toggle the loopqueue-mode", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        Scheduler scheduler = player.getScheduler();

        if (scheduler.isLoop())
            return send(error(event.translate("command.control.disable.loop.title"), event.translate("command.control.disable.loop.description")));

        if (!scheduler.isLoopqueue()) {
            scheduler.setLoopqueue(true);
            return send(success(event.translate("command.queueloop.enabled.title"), event.translate("command.queueloop.enabled.description")));
        }

        scheduler.setLoopqueue(false);
        return send(success(event.translate("command.queueloop.disabled.title"), event.translate("command.queueloop.disabled.description")));
    }
}
