package io.groovybot.bot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;
import net.dv8tion.jda.core.utils.Helpers;

import java.util.LinkedList;

public class RemoveCommand extends SameChannelCommand {

    public RemoveCommand() {
        super(new String[]{"remove", "rm"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you remove a specific song", "<index>");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0)
            return sendHelp();
        if (!Helpers.isNumeric(args[0]))
            return send(error(event.translate("phrases.invalidnumber.title"), event.translate("phrases.invalidnumber.description")));
        int query = Integer.parseInt(args[0]) - 1;
        // TODO: FIX BUG THAT CANNOT REMOVE 1
        System.out.println(query);
        System.out.println(player.trackQueue.size());
        if (query > player.trackQueue.size() || query < 1)
            return send(error(event.translate("command.remove.notinqueue.title"), event.translate("command.remove.notinqueue.description")));
        ((LinkedList<AudioTrack>) player.trackQueue).remove(query);
        return send(success(event.translate("command.remove.removed.title"), String.format(event.translate("command.remove.removed.description"), query + 1)));
    }
}
