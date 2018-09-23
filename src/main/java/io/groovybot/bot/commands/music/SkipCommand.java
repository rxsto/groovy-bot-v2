package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class SkipCommand extends SameChannelCommand {
    public SkipCommand() {
        super(new String[] {"skip", "s"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets you skip the current track", "[position]");
    }

    @Override
    public Result execute(String[] args, CommandEvent event) {
        if (event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
            return super.execute(args, event);

        final MusicPlayer player = getPlayer(event.getGuild(), event.getChannel());
        if (player.checkConnect(event)) {
            player.connect(event.getMember().getVoiceState().getChannel());
            return runCommand(args, event);
        }

        return send(error(event));
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event) {
        MusicPlayer player = getPlayer(event.getGuild(), event.getChannel());
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        if (args.length == 1) {
            for (int x = Integer.parseInt(args[0]); x > 1; x--)
                player.trackQueue.remove();
            return send(error(event.translate("command.skip.success.title"), String.format(event.translate("command.skip.success.one.description"), args[0])));
        }
        player.stop();
        return send(error(event.translate("command.skip.success.title"), (event.translate("command.skip.success.more.description"))));
    }
}
