/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

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
