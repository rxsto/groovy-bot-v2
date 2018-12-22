/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergeij Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
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

package co.groovybot.bot.core.audio.deezer.source;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.zeloon.deezer.client.DeezerClient;
import com.zeloon.deezer.domain.Playlist;
import com.zeloon.deezer.domain.Track;
import com.zeloon.deezer.domain.internal.PlaylistId;
import com.zeloon.deezer.domain.internal.TrackId;
import com.zeloon.deezer.io.HttpResourceConnection;
import co.groovybot.bot.core.audio.AudioTrackFactory;
import co.groovybot.bot.core.audio.spotify.entities.track.TrackData;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
public class DeezerSourceManager implements AudioSourceManager {

    @Getter
    private final DeezerClient deezerClient;
    @Getter
    private final AudioTrackFactory audioTrackFactory;

    private static final Pattern TRACK_PATTERN = Pattern.compile("https?://.*\\.deezer\\.com/.*/track/([0-9]*)");
    private static final Pattern PLAYLIST_PATTERN = Pattern.compile("https?://.*\\.deezer\\.com/.*/playlist/([0-9]*)");

    public DeezerSourceManager() {
        this.deezerClient = new DeezerClient(new HttpResourceConnection());
        this.audioTrackFactory = new AudioTrackFactory();
    }

    @Override
    public String getSourceName() {
        return "Deezer Source Manager";
    }

    @Override
    public AudioItem loadItem(DefaultAudioPlayerManager manager, AudioReference reference) {
        if (reference.identifier.startsWith("ytsearch:") || reference.identifier.startsWith("scsearch:")) return null;
        try {
            URL url = new URL(reference.identifier);
            if (!url.getHost().equalsIgnoreCase("www.deezer.com"))
                return null;
            String rawUrl = url.toString();
            AudioItem audioItem = null;

            if (TRACK_PATTERN.matcher(rawUrl).matches())
                audioItem = buildTrack(rawUrl);
            if (PLAYLIST_PATTERN.matcher(rawUrl).matches())
                audioItem = buildPlaylist(rawUrl);
            return audioItem;
        } catch (MalformedURLException e) {
            log.error("Failed to load the item!", e);
            return null;
        }
    }

    private AudioTrack buildTrack(String url) {
        TrackId trackId = new TrackId(Long.valueOf(parseTrackPattern(url)));
        Track track = this.deezerClient.get(trackId);
        TrackData trackData = this.getTrackData(track);
        return this.audioTrackFactory.getAudioTrack(trackData);
    }

    private AudioPlaylist buildPlaylist(String url) {
        PlaylistId playlistId = new PlaylistId(Long.valueOf(parsePlaylistPattern(url)));
        Playlist playlist = this.deezerClient.get(playlistId);
        List<Track> playlistTracks = playlist.getTracks().getData();
        List<TrackData> trackDatas = this.getPlaylistTrackData(playlistTracks);
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDatas);
        return new BasicAudioPlaylist(playlist.getTitle(), audioTracks, null, false);
    }

    private List<TrackData> getPlaylistTrackData(List<Track> playlistTracks) {
        return playlistTracks.stream()
                .map(this::getTrackData)
                .collect(Collectors.toList());
    }

    private TrackData getTrackData(Track track) {
        return new TrackData(
                track.getTitle(),
                track.getLink(),
                Collections.singletonList(track.getArtist().getName()),
                track.getDuration()
        );
    }

    private String parseTrackPattern(String identifier) {
        final Matcher matcher = TRACK_PATTERN.matcher(identifier);

        if (!matcher.find())
            return "noTrackId";
        return matcher.group(1);
    }

    private String parsePlaylistPattern(String identifier) {
        final Matcher matcher = PLAYLIST_PATTERN.matcher(identifier);

        if (!matcher.find())
            return "noPlaylistId";
        return matcher.group(1);
    }

    @Override
    public boolean isTrackEncodable(AudioTrack audioTrack) {
        return false;
    }

    @Override
    public void encodeTrack(AudioTrack audioTrack, DataOutput dataOutput) {
        throw new UnsupportedOperationException("encodeTrack is unsupported.");
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo audioTrackInfo, DataInput dataInput) {
        throw new UnsupportedOperationException("decodeTrack is unsupported.");
    }

    @Override
    public void shutdown() {
    }
}
