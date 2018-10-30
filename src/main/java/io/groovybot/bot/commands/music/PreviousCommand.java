package io.groovybot.bot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.audio.QueuedTrack;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

import java.util.LinkedList;

public class PreviousCommand extends SameChannelCommand {
    public PreviousCommand() {
        super(new String[]{"previous", "back"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets you play the last played song", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (player.getPreviousTrack() == null)
            return send(error(event.translate("command.previous.notrack.title"), event.translate("command.previous.notrack.description")));

        ((LinkedList<QueuedTrack>) player.getTrackQueue()).addFirst((QueuedTrack) player.getPlayer().getPlayingTrack());

        player.play(player.getPreviousTrack());
        player.setPreviousTrack(null);
        return send(success(event.translate("command.previous.title"), event.translate("command.previous.description")));
    }
}
