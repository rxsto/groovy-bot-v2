package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.audio.Scheduler;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;
import co.groovybot.bot.core.entity.EntityProvider;

public class LoopCommand extends SameChannelCommand {

    public LoopCommand() {
        super(new String[]{"loop", "lp"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you toggle through all loop-modes", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        Scheduler scheduler = player.getScheduler();

        if (!scheduler.isLoop() && !scheduler.isLoopqueue()) {
            scheduler.setLoop(true);
            return send(noTitle("\uD83D\uDD02 " + event.translate("command.loop.song")));
        } else if (scheduler.isLoop()) {
            if (!Permissions.tierOne().isCovered(EntityProvider.getUser(event.getAuthor().getIdLong()).getPermissions(), event)) {
                scheduler.setLoop(false);
                send(noTitle("ℹ " + event.translate("command.loop.none")));
                return send(info(event.translate("command.loop.premium.title"), event.translate("command.loop.premium.description")));
            } else {
                scheduler.setLoop(false);
                scheduler.setLoopqueue(true);
                return send(noTitle("\uD83D\uDD01 " + event.translate("command.loop.queue")));
            }
        } else if (scheduler.isLoopqueue()) {
            scheduler.setLoopqueue(false);
            return send(noTitle("ℹ " + event.translate("command.loop.none")));
        } else {
            return send(error(event));
        }
    }
}
