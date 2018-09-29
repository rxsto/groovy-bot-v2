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

public class MoveCommand extends SameChannelCommand {

    public MoveCommand() {
        super(new String[]{"move", "mv"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you move a song from one position to another", "<song> <position>");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length != 2 || !Helpers.isNumeric(args[0]) || !Helpers.isNumeric(args[1]))
            return sendHelp();

        int songPos = Integer.parseInt(args[0]);
        int wantPos = Integer.parseInt(args[1]);

        if (songPos > player.getTrackQueue().size() || wantPos > player.getTrackQueue().size() || songPos < 1 || wantPos < 1)
            return send(error(event.translate("phrases.invalidnumbers.title"), event.translate("phrases.invalidnumbers.description")));

        if (songPos == wantPos)
            return send(error(event.translate("phrases.samenumbers.title"), event.translate("phrases.samenumbers.description")));

        LinkedList<AudioTrack> trackQueue = (LinkedList<AudioTrack>) player.getTrackQueue();

        int songPosIndex = songPos - 1;
        int wantPosIndex = wantPos - 1;

        AudioTrack preSave = trackQueue.get(songPosIndex);
        trackQueue.remove(songPosIndex);
        trackQueue.add(wantPosIndex, preSave);

        return send(success(event.translate("command.move.title"), String.format(event.translate("command.move.description"), preSave.getInfo().title, wantPos)));
    }
}
