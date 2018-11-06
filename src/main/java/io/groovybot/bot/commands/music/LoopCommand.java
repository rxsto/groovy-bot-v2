package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.audio.Scheduler;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;
import io.groovybot.bot.core.entity.EntityProvider;

public class LoopCommand extends SameChannelCommand {

    public LoopCommand() {
        super(new String[]{"loop", "lp"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you toggle the loop-modes", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        Scheduler scheduler = player.getScheduler();

        if (!scheduler.isLoop() && !scheduler.isLoopqueue()) {
            scheduler.setLoop(true);
            return send(info(event.translate("command.loop.title"), event.translate("command.loop.loop.description")));
        } else if (scheduler.isLoop()) {
            if (!Permissions.tierOne().isCovered(EntityProvider.getUser(event.getAuthor().getIdLong()).getPermissions(), event)) {
                scheduler.setLoop(false);
                return send(info(event.translate("command.loop.none.title"), event.translate("command.loop.none.description")).addField(event.translate("phrases.text.information"), "▫ " + event.translate("command.loop.nopremium.info"), false));
            } else {
                scheduler.setLoop(false);
                scheduler.setLoopqueue(true);
                return send(info(event.translate("command.loop.title"), event.translate("command.loop.loopqueue.description")));
            }
        } else if (scheduler.isLoopqueue()) {
            scheduler.setLoopqueue(false);
            return send(info(event.translate("command.loop.title"), event.translate("command.loop.none.description")));
        } else {
            return send(error(event));
        }
    }
}
