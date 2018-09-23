package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.InChannelCommand;

public class JoinCommand extends InChannelCommand {

    public JoinCommand() {
        super(new String[] {"join"}, CommandCategory.MUSIC, Permissions.everyone(), "Let's the bot join a channel", "");
    }

    @Override
    public Result execute(String[] args, CommandEvent event) {
        MusicPlayer player = getPlayer(event.getGuild(), event.getChannel());
        if (player.checkConnect(event)) {
            player.connect(event.getMember().getVoiceState().getChannel());
            return send(success(event.translate("command.join.joined.title"), event.translate("command.join.joined.description")));
        }
        return null;
    }
}
