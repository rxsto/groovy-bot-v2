package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class SkipCommand extends SameChannelCommand {
    public SkipCommand() {
        super(new String[]{"skip", "s", "next"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you skip the current/to a specific track", "[position]");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));

        player.setPreviousTrack(player.getPlayer().getPlayingTrack());

        int skipTo;

        if (args.length == 0)
            skipTo = 1;
        else {
            try {
                skipTo = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return send(error(event.translate("phrases.invalidnumber.title"), event.translate("phrases.invalidnumber.description")));
            }
        }

        player.skipTo(skipTo);

        if (args.length > 0)
            return send(success(event.translate("command.skip.success.title"), String.format(event.translate("command.skip.success.more.description"), skipTo)));

        return send(success(event.translate("command.skip.success.title"), (event.translate("command.skip.success.one.description"))));
    }
}
