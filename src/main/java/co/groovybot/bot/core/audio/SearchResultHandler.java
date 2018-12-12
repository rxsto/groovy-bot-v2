package co.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Log4j2
public class SearchResultHandler implements AudioLoadResultHandler {

    private Exception exception;
    private AudioPlaylist playlist;

    public AudioPlaylist searchSync(AudioPlayerManager audioPlayerManager, String query, int timeout) throws PlaylistSearchException {
        try {
            audioPlayerManager.loadItem("ytsearch: " + query, this).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            this.exception = e;
        } catch (TimeoutException e) {
            log.error("Searching for " + query + " timed out after " + timeout + "ms!", e);
        }

        if (this.exception != null) {
            throw new PlaylistSearchException("Failed to search for query " + query, exception);
        }

        if (this.playlist == null)
            throw new PlaylistSearchException("The playlist is unexpectedly null");
        return playlist;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        this.exception = new UnsupportedOperationException("Can not load a playlist, if we expecting a playlist!");
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        this.playlist = playlist;
    }

    @Override
    public void noMatches() {
        this.playlist = new BasicAudioPlaylist("No matches", Collections.emptyList(), null, true);
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        this.exception = exception;
    }

    public static class PlaylistSearchException extends Exception {

        PlaylistSearchException(String message) {
            super(message);
        }

        PlaylistSearchException(String message, Exception cause) {
            super(message, cause);
        }
    }
}
