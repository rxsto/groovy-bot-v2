package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.audio.MusicPlayerManager;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;
import io.groovybot.bot.core.entity.EntityProvider;
import lombok.extern.log4j.Log4j;


@Log4j
public class PlayCommand extends SameChannelCommand {

    public PlayCommand() {
        super(new String[] {"play", "p", "add"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets you play any music you want", "<song/url>");
    }

    @Override
    public Result execute(String[] args, CommandEvent event) {
        final MusicPlayer player = getPlayer(event.getGuild(), event.getChannel());
        if (args.length == 0) {
            if (player.isPaused()) {
                player.resume();
                return send(success(event.translate("command.resume.title"), event.translate("command.resume.description")));
            }
            return send(error(event.translate("phrases.invalidargument.title"), event.translate("phrases.invalidargument.description")));
        }

        if (event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
            return super.execute(args, event);

        if (player.checkConnect(event)) {
            player.connect(event.getMember().getVoiceState().getChannel());
            return runCommand(args, event);
        }

        return send(error(event));
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event) {
        getPlayer(event.getGuild(), event.getChannel()).queueSongs(event, false);
        return null;
    }
}
