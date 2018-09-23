package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class LeaveCommand extends SameChannelCommand {

    public LeaveCommand() {
        super(new String[] {"leave", "l"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets the bot leave the channel", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event) {
        MusicPlayer player = getPlayer(event.getGuild(), event.getChannel());
        player.stop();
        player.getLink().disconnect();
        return send(success(event.translate("command.leave.left.title"), event.translate("command.leave.left.description")));
    }
}
