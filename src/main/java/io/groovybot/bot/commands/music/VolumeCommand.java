package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class VolumeCommand extends SameChannelCommand {
    public VolumeCommand() {
        super(new String[]{"volume", "vol"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you set Groovy's volume", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        int volume;
        if (args.length == 0)
            return send(info(event.translate("command.volume.current.title"), String.format(event.translate("command.volume.current.description"), player.getPlayer().getVolume())));
        try {
            volume = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return send(error(event.translate("phrases.invalidnumber.title"), event.translate("phrases.invalidnumber.description")));

        }
        if (volume > 200 || volume < 0)
            return send(error(event.translate("command.volume.tohigh.title"), event.translate("command.volume.tohigh.description")));
        player.setVolume(volume);
        return send(success(event.translate("command.volume.set.title"), String.format(event.translate("command.volume.set.description"), volume)));
    }
}
