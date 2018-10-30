package io.groovybot.bot.core.audio;

import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import io.groovybot.bot.util.EmbedUtil;
import io.groovybot.bot.util.SafeMessage;
import lavalink.client.player.event.AudioEventAdapterWrapped;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;


@Log4j2
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
    @Getter
    @Setter
    private boolean autoPlay = false;

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
                Guild guild = ((MusicPlayer) player).getGuild();

                if (guild.getSelfMember().getVoiceState().getChannel().getMembers().size() == 1) {
                    player.stop();
                    ((MusicPlayer) player).leave();
                    return;
                }

                AudioTrack nextTrack = null;

                if (repeating) {
                    track.setPosition(0);
                    nextTrack = track;
                }

                if (queueRepeating)
                    player.trackQueue.add((QueuedTrack) track);

                if (shuffle) {
                    if (player.trackQueue.isEmpty()) {
                        player.onEnd(true);
                    }

                    final int index = ThreadLocalRandom.current().nextInt(player.trackQueue.size());
                    nextTrack = ((LinkedList<QueuedTrack>) player.trackQueue).get(index);
                    ((LinkedList<QueuedTrack>) player.trackQueue).remove(index);
                }

                ((MusicPlayer) player).setPreviousTrack(track);

                if (autoPlay) {
                    runAutoplay(track);
                    return;
                }

                if (!repeating && !shuffle)
                    nextTrack = player.pollTrack();

                if (nextTrack == null)
                    player.onEnd(true);

                player.play(nextTrack, false);
                break;

            case LOAD_FAILED:
                player.play(player.pollTrack(), true);
                break;

            default:
                break;
        }
    }

    public void runAutoplay(AudioTrack track) {
        Message infoMessage = player.announceAutoplay();
        try {
            SearchResult result = player.youtubeClient.retrieveRelatedVideos(track.getIdentifier());
            SafeMessage.editMessage(infoMessage, EmbedUtil.success("Loaded video", String.format("Successfully loaded video `%s`", result.getSnippet().getTitle())));
            queueSearchResult(result, infoMessage);
        } catch (IOException e) {
            SafeMessage.editMessage(infoMessage, EmbedUtil.error("Unknown error", "An unknown autoplay-error occurred while retrieving the next video!"));
            log.error("[Scheduler] Error while retrieving autoplay video", e);
        }
    }

    private void queueSearchResult(SearchResult result, Message infoMessage) {
        player.getAudioPlayerManager().loadItem("https://youtube.com/watch?v=" + result.getId().getVideoId(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                player.play(track, false);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
            }

            @Override
            public void noMatches() {
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                SafeMessage.editMessage(infoMessage, EmbedUtil.error("Unknown error", "An unknown error occurred while queueing song!"));
                log.error("[AutoPlay] Error while queueing song", exception);
            }
        });
    }
}
