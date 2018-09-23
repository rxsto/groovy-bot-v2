package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lavalink.client.player.event.AudioEventAdapterWrapped;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Scheduler extends AudioEventAdapterWrapped {

    private final Player player;
    @Getter
    @Setter
    private boolean queueRepeating = false;
    @Getter
    @Setter
    private boolean repeating = false;

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
                    player.play(track);
                    return;
                }
                if (queueRepeating && !reason.equals(AudioTrackEndReason.REPLACED))
                    player.trackQueue.add(track);
                AudioTrack nextTrack = player.pollTrack();
                if (nextTrack == null)
                    player.onEnd(true);
                player.play(nextTrack);
                break;
            case LOAD_FAILED:
                player.play(player.pollTrack());
                break;
        }
    }
}
