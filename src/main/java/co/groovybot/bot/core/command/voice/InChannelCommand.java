package co.groovybot.bot.core.command.voice;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

public abstract class InChannelCommand extends Command {

    public InChannelCommand(String[] aliases, CommandCategory commandCategory, Permissions permissions, String description, String usage) {
        super(aliases, commandCategory, permissions, description, usage);
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (!event.getGuild().getMember(event.getAuthor()).getVoiceState().inVoiceChannel())
            return send(error(event.translate("phrases.notconnected.title"), event.translate("phrases.notconnected.description")));
        return execute(args, event, getPlayer(event.getGuild(), event.getChannel()));
    }

    public abstract Result execute(String[] args, CommandEvent event, MusicPlayer player);

    private MusicPlayer getPlayer(Guild guild, TextChannel channel) {
        return GroovyBot.getInstance().getMusicPlayerManager().getPlayer(guild, channel);
    }
}
