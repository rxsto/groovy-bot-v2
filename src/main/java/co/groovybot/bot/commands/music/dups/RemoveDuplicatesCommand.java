package co.groovybot.bot.commands.music.dups;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.SubCommand;
import co.groovybot.bot.core.command.permission.Permissions;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RemoveDuplicatesCommand extends SubCommand {

    public RemoveDuplicatesCommand() {
        super(new String[]{"remove", "rm"}, Permissions.djMode(), "Removes all duplicates from the queue.", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
            return send(error(event.translate("phrases.notinchannel.title"), event.translate("phrases.notinchannel.description")));
        if (!event.getGuild().getSelfMember().getVoiceState().getChannel().equals(event.getMember().getVoiceState().getChannel()))
            return send(error(event.translate("phrases.notsamechannel.title"), event.translate("phrases.notsamechannel.description")));
        MusicPlayer musicPlayer = GroovyBot.getInstance().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());
        int dups = musicPlayer.removeDups();
        return send(success(event.translate("command.dups.removed.title"), event.translate(String.format("command.dups.removed.description", dups))));
    }
}
