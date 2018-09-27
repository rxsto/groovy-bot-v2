package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class LoopQueueCommand extends SameChannelCommand {

    public LoopQueueCommand() {
        super(new String[]{"loopqueue", "lq", "looprepeat", "lr"}, CommandCategory.MUSIC, Permissions.djMode(), "Loops the whole queue", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!player.getScheduler().isQueueRepeating()) {
            player.getScheduler().setQueueRepeating(true);
            return send(success(event.translate("command.queueloop.enabled.title"), event.translate("command.queueloop.enabled.description")));
        }
        player.getScheduler().setQueueRepeating(false);
        return send(success(event.translate("command.queueloop.disabled.title"), event.translate("command.queueloop.disabled.description")));
    }
}
