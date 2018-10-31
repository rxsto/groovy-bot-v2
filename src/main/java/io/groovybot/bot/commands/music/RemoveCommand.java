package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.audio.QueuedTrack;
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
        int query = Integer.parseInt(args[0]);
        if (query > player.trackQueue.size() || query < 1)
            return send(error(event.translate("command.remove.notinqueue.title"), event.translate("command.remove.notinqueue.description")));
        ((LinkedList<QueuedTrack>) player.trackQueue).remove(query - 1);
        return send(success(event.translate("command.remove.removed.title"), String.format(event.translate("command.remove.removed.description"), query)));
    }
}
