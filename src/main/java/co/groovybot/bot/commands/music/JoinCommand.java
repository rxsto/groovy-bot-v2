package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.InChannelCommand;

public class JoinCommand extends InChannelCommand {

    public JoinCommand() {
        super(new String[]{"join", "j", "summon"}, CommandCategory.MUSIC, Permissions.everyone(), "Summons Groovy into your voicechannel", "");
    }

    @Override
    public Result execute(String[] args, CommandEvent event, MusicPlayer player) {
        if (player.checkConnect(event)) {
            player.connect(event.getMember().getVoiceState().getChannel());
            return send(success(event.translate("command.join.joined.title"), event.translate("command.join.joined.description")));
        } else return null;
    }
}
