package io.groovybot.bot.core.command.voice;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import net.dv8tion.jda.core.entities.Guild;

public abstract class InChannelCommand extends Command {

    public InChannelCommand(String[] aliases, CommandCategory commandCategory, Permissions permissions, String description, String usage) {
        super(aliases, commandCategory, permissions, description, usage);
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (!event.getGuild().getMember(event.getAuthor()).getVoiceState().inVoiceChannel())
            return send(error(event.translate("phrases.notconnected.title"), event.translate("phrases.notconnected.description")));
        return execute(args, event);
    }

    public abstract Result execute(String[] args, CommandEvent event);

    protected MusicPlayer getPlayer(Guild guild) {
        return GroovyBot.getInstance().getMusicPlayerManager().getPlayer(guild);
    }
}
