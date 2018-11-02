package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.audio.Scheduler;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class AutoPlayCommand extends SameChannelCommand {

    public AutoPlayCommand() {
        super(new String[]{"autoplay", "auto", "ap"}, CommandCategory.MUSIC, Permissions.tierOne(), "Lets you toggle autoplay-mode", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        Scheduler scheduler = player.getScheduler();
        if (scheduler.isAutoPlay()) {
            scheduler.setAutoPlay(false);
            return send(success(event.translate("command.autoplay.disabled.title"), event.translate("command.autoplay.disabled.description")));
        }
        scheduler.setAutoPlay(true);
        return send(success(event.translate("command.autoplay.enabled.title"), event.translate("command.autoplay.enabled.description")));
    }
}
