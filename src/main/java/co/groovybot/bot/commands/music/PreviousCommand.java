package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.LinkedList;

public class PreviousCommand extends SameChannelCommand {
    public PreviousCommand() {
        super(new String[]{"previous", "back"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets Groovy play the previous played song", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (player.getPreviousTrack() == null)
            return send(error(event.translate("command.previous.notrack.title"), event.translate("command.previous.notrack.description")));

        ((LinkedList<AudioTrack>) player.getTrackQueue()).addFirst(player.getPlayer().getPlayingTrack());

        player.play(player.getPreviousTrack());
        player.setPreviousTrack(null);
        return send(success(event.translate("command.previous.title"), event.translate("command.previous.description")));
    }
}
