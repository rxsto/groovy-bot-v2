package co.groovybot.bot.core.command.voice;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;

public abstract class SameChannelCommand extends InChannelCommand {

    public SameChannelCommand(String[] aliases, CommandCategory commandCategory, Permissions permissions, String description, String usage) {
        super(aliases, commandCategory, permissions, description, usage);
    }

    @Override
    public Result execute(String[] args, CommandEvent event, MusicPlayer player) {
        if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
            return send(error(event.translate("phrases.notinchannel.title"), event.translate("phrases.notinchannel.description")));
        if (!event.getGuild().getSelfMember().getVoiceState().getChannel().equals(event.getMember().getVoiceState().getChannel()))
            return send(error(event.translate("phrases.notsamechannel.title"), event.translate("phrases.notsamechannel.description")));
        return runCommand(args, event, player);
    }

    public abstract Result runCommand(String[] args, CommandEvent event, MusicPlayer player);
}
