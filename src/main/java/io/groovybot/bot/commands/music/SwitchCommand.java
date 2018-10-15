package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.InChannelCommand;

public class SwitchCommand extends InChannelCommand {
    public SwitchCommand() {
        super(new String[]{"switch"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you to switch the default text-channel for messages", "");
    }

    @Override
    public Result execute(String[] args, CommandEvent event, MusicPlayer player) {
        if (player.getChannel().getIdLong() == event.getChannel().getIdLong())
            return send(error(event.translate("command.switch.already.title"), event.translate("command.switch.already.description")));

        player.setChannel(event.getChannel());

        if (event.getMember().getVoiceState().getChannel().getIdLong() != event.getGuild().getSelfMember().getVoiceState().getChannel().getIdLong()) {
            player.connect(event.getMember().getVoiceState().getChannel());
            return send(success(event.translate("command.switch.voice.title"), event.translate("command.switch.voice.description")));
        }

        return send(success(event.translate("command.switch.title"), event.translate("command.switch.description")));
    }
}
