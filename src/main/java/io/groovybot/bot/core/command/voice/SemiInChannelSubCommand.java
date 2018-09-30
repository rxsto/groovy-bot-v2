package io.groovybot.bot.core.command.voice;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.SubCommand;
import io.groovybot.bot.core.command.permission.Permissions;

public abstract class SemiInChannelSubCommand extends SubCommand {

    public SemiInChannelSubCommand(String[] aliases, Permissions permissions, String description, String usage) {
        super(aliases, permissions, description, usage);
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        MusicPlayer player =event.getGroovyBot().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());
        if (event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
            if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
                return send(error(event.translate("phrases.notinchannel.title"), event.translate("phrases.notinchannel.description")));
            if (!event.getGuild().getSelfMember().getVoiceState().getChannel().equals(event.getMember().getVoiceState().getChannel()))
                return send(error(event.translate("phrases.notsamechannel.title"), event.translate("phrases.notsamechannel.description")));
            return executeCommand(args, event, player);
        }

        if (player.checkConnect(event)) {
            player.connect(event.getMember().getVoiceState().getChannel());
            return executeCommand(args, event, player);
        }
        return send(error(event));
    }

    protected abstract Result executeCommand(String[] args, CommandEvent event, MusicPlayer player);

}
