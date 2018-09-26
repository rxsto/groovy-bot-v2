package io.groovybot.bot.core.command.voice;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public abstract class SemiInChannelCommand extends SameChannelCommand {

    public SemiInChannelCommand(String[] aliases, CommandCategory commandCategory, Permissions permissions, String description, String usage) {
        super(aliases, commandCategory, permissions, description, usage);
    }

    @Override
    public Result execute(String[] args, CommandEvent event) {
        MusicPlayer player = getPlayer(event.getGuild(), event.getChannel());
        if (event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
            return super.execute(args, event);

        if (player.checkConnect(event)) {
            player.connect(event.getMember().getVoiceState().getChannel());
            return execute(args, event, player);
        }
        return send(error(event));
    }

    protected abstract Result execute(String[] args, CommandEvent event, MusicPlayer player);

    @Override
    public Result runCommand(String[] args, CommandEvent event) {
        return execute(args, event, getPlayer(event.getGuild(), event.getChannel()));
    }
}
