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
    public Result execute(String[] args, CommandEvent event, MusicPlayer player) {
        if (event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
            return super.execute(args, event, player);

        if (player.checkConnect(event)) {
            player.connect(event.getMember().getVoiceState().getChannel());
            return executeCommand(args, event, player);
        }

        return null;
    }

    protected abstract Result executeCommand(String[] args, CommandEvent event, MusicPlayer player);

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        return executeCommand(args, event, player);
    }
}
