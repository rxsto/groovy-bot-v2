package io.groovybot.bot.core.audio;

import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import io.groovybot.bot.util.EmbedUtil;
import lavalink.client.player.event.AudioEventAdapterWrapped;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import net.dv8tion.jda.core.entities.Message;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;


@Log4j
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
                if (autoPlay) {
                    Message infoMessaege = player.announceAutoplay(player);
                    try {
                        SearchResult result = player.youtubeClient.retrieveRelatedVideos(track.getIdentifier());
                        infoMessaege.editMessage(EmbedUtil.success("Loaded video", String.format("Successfully loaded video `%s`", result.getSnippet().getTitle())).build()).queue();
                        queueSearchResult(result, infoMessaege);
                    } catch (IOException e) {
                        infoMessaege.editMessage(EmbedUtil.error("Unkown error", "An unexpedted error occurred while retieving autplay video").build()).queue();
                        log.error("[Scheduler] Error while retieving autplay video", e);
                    }
                    return;
                }
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
                break;
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
                System.out.println("playlists");
            }

            @Override
            public void noMatches() {
                System.out.println("no matched");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                infoMessage.editMessage(EmbedUtil.error("Unkown error", "An unkown error occcurred while queueing song").build()).queue();
                log.error("[AutoPlay] Error while queueing song", exception);
            }
        });
    }
}
