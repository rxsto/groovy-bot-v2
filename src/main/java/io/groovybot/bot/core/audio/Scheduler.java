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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Log4j2
@RequiredArgsConstructor
public class Scheduler extends AudioEventAdapterWrapped {

    private static final Pattern TRACK_PATTERN = Pattern.compile("https?://.*\\.youtube\\.com/watch\\?v=([^?/&]*)");

    private final Player player;
    @Getter
    @Setter
    private boolean loopqueue = false;
    @Getter
    @Setter
    private boolean loop = false;
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

                if (guild.getSelfMember().getVoiceState().getChannel() == null)
                    return;

                // Leave if bot alone
                if (guild.getSelfMember().getVoiceState().getChannel().getMembers().size() == 1) {
                    player.stop();
                    ((MusicPlayer) player).leave();
                    return;
                }

                AudioTrack nextTrack = null;

                // Loop-mode (repeat ended song)
                if (loop) {
                    player.play(track);
                    return;
                }

                // Loopqueue-mode (add track to end of queue)
                if (loopqueue) player.trackQueue.add(track);

                // Shuffle-mode
                if (shuffle) {
                    if (player.trackQueue.isEmpty())
                        player.onEnd(true);
                    else {
                        final int index = ThreadLocalRandom.current().nextInt(player.trackQueue.size());
                        nextTrack = ((LinkedList<AudioTrack>) player.trackQueue).get(index);
                        ((LinkedList<AudioTrack>) player.trackQueue).remove(index);
                    }
                }

                // Check for autoplay (only use it if queue empty)
                if (autoPlay == player.trackQueue.isEmpty()) {
                    runAutoplay(track);
                    return;
                }

                // If not loop and not shuffle get next track and remove it from queue
                if (!shuffle) nextTrack = player.pollTrack();

                // Set previous-track to ended song
                if (!loopqueue) ((MusicPlayer) player).setPreviousTrack(track);

                // If no nexttrack end and leave else play nexttrack
                if (nextTrack == null) player.onEnd(true);
                else player.play(nextTrack, false);
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

        final Matcher matcher = TRACK_PATTERN.matcher(track.getInfo().uri);

        if (!matcher.find()) {
            SafeMessage.editMessage(infoMessage, EmbedUtil.error("Not a YouTube-Track!", "We **couldn't search** for a AutoPlay-Track as the **previos** track was **not a YouTube-Track**!"));
            return;
        }

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
                player.play(playlist.getTracks().get(0), false);
            }

            @Override
            public void noMatches() {
                SafeMessage.editMessage(infoMessage, EmbedUtil.error("Couldn't find an AutoPlay-Track!", "We're sorry, but there **wasn't any result**! **Please try again**!"));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                SafeMessage.editMessage(infoMessage, EmbedUtil.error("Unknown error", "An **unknown error** occurred while **queueing** song!"));
                log.error("[AutoPlay] Error while queueing song", exception);
            }
        });
    }
}
