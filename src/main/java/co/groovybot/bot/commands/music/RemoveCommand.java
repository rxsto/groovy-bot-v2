package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;
import co.groovybot.bot.util.EmbedUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.utils.Helpers;

import java.util.LinkedList;

public class RemoveCommand extends SameChannelCommand {

    public RemoveCommand() {
        super(new String[]{"remove", "rm"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you remove a specific song from the queue", "<index>");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0)
            return sendHelp();
        if (!Helpers.isNumeric(args[0]))
            return send(EmbedUtil.error(event.translate("phrases.invalidnumber.title"), event.translate("phrases.invalidnumber.description")));
        int query = Integer.parseInt(args[0]);
        if (query > player.trackQueue.size() || query < 1)
            return send(EmbedUtil.error(event.translate("command.remove.notinqueue.title"), event.translate("command.remove.notinqueue.description")));
        ((LinkedList<AudioTrack>) player.trackQueue).remove(query - 1);
        return send(EmbedUtil.success(event.translate("command.remove.removed.title"), String.format(event.translate("command.remove.removed.description"), query)));
    }
}
