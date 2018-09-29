package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lavalink.client.player.event.AudioEventAdapterWrapped;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;


@RequiredArgsConstructor
public class Scheduler extends AudioEventAdapterWrapped {

    private final Player player;
    @Getter
    @Setter
    private boolean queueRepeating = false;
    @Getter
    @Setter
    private boolean repeating = false;
    @Getter
    @Setter
    private boolean shuffle = false;


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
                if (repeating) {
                    track.setPosition(0);
                    player.play(track, false);
                    break;
                }
                if (queueRepeating) {
                    player.trackQueue.add(track);
                }
                if (shuffle) {
                    final int index = ThreadLocalRandom.current().nextInt(player.trackQueue.size());
                    player.play(((LinkedList<AudioTrack>) player.trackQueue).get(index), false);
                    ((LinkedList<AudioTrack>) player.trackQueue).remove(index);
                    return;
                }
                AudioTrack nextTrack = player.pollTrack();
                if (nextTrack == null)
                    player.onEnd(true);
                player.play(nextTrack, false);
                break;
            case LOAD_FAILED:
                player.play(player.pollTrack(), true);
                break;
            default:
                // Do nothing
                break;
        }
    }
}
