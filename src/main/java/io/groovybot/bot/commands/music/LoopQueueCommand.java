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
        super(new String[]{"loopqueue", "lq", "looprepeat", "lr"}, CommandCategory.MUSIC, Permissions.tierOne(), "Loops the whole queue", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        Scheduler scheduler = player.getScheduler();
        if (scheduler.isShuffle() || scheduler.isRepeating())
            return send(error(event.translate("controlpanel.disable.loopshuffle.title"), event.translate("controlpanel.disable.loopshuffle.description")));
        if (!scheduler.isQueueRepeating()) {
            scheduler.setQueueRepeating(true);
            return send(success(event.translate("command.queueloop.enabled.title"), event.translate("command.queueloop.enabled.description")));
        }
        scheduler.setQueueRepeating(false);
        return send(success(event.translate("command.queueloop.disabled.title"), event.translate("command.queueloop.disabled.description")));
    }
}
