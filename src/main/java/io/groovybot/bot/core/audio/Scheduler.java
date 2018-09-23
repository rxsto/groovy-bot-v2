package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import io.groovybot.bot.util.EmbedUtil;
import lavalink.client.player.event.AudioEventAdapterWrapped;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.core.entities.TextChannel;

@RequiredArgsConstructor
public class Scheduler extends AudioEventAdapterWrapped {

    private final Player player;
    @Getter
    @Setter
    private boolean queueRepeating;
    @Getter
    @Setter
    private boolean repeating;

    @Override
    public void onTrackStart(AudioPlayer audioPlayer, AudioTrack track) {
        player.announceSong(audioPlayer, track);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        handleTrackEnd(track, endReason);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        handleTrackEnd(track, AudioTrackEndReason.LOAD_FAILED);
    }

    private void handleTrackEnd(AudioTrack track, AudioTrackEndReason reason) {
        switch (reason) {
            case FINISHED:
                AudioTrack nextTrack = player.pollTrack();
                if (nextTrack == null)
                    player.disconnect();
                if (queueRepeating)
                    player.trackQueue.add(track);
                if (repeating) {
                    player.play(track);
                    return;
                }
                player.play(nextTrack);
                break;
            case STOPPED:
            case LOAD_FAILED:
                player.play(player.pollTrack());
                break;
        }
    }
}
