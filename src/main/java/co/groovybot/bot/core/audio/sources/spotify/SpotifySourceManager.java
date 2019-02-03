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

package co.groovybot.bot.core.audio.sources.spotify;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.AudioTrackFactory;
import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.audio.data.AlbumData;
import co.groovybot.bot.core.audio.data.ArtistData;
import co.groovybot.bot.core.audio.data.PlaylistData;
import co.groovybot.bot.core.audio.data.TrackData;
import co.groovybot.bot.core.audio.sources.spotify.entities.keys.AlbumKey;
import co.groovybot.bot.core.audio.sources.spotify.entities.keys.ArtistKey;
import co.groovybot.bot.core.audio.sources.spotify.entities.keys.PlaylistKey;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class SpotifySourceManager implements AudioSourceManager {

    private static final Pattern PLAYLIST_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/playlists?/([^?/\\s]*)");
    private static final Pattern TRACK_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/tracks?/([^?/\\s]*)");
    private static final Pattern ALBUM_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/albums?/([^?/\\s]*)");
    private static final Pattern TOPTEN_ARTIST_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/artists?/([^?/\\s]*)");
    private static final String SERVICE_BASE_URL = GroovyBot.getInstance().getConfig().getJSONObject("kereru").getString("host");

    @Getter
    private final OkHttpClient httpClient;
    @Getter
    private final AudioTrackFactory audioTrackFactory;
    @Getter
    @Setter
    private MusicPlayer player;

    private int playlistRequestExecutionCount = 0;
    private int filteredLocalTracks = 0;

    public SpotifySourceManager() {
        this.httpClient = new OkHttpClient.Builder().build();
        this.audioTrackFactory = new AudioTrackFactory();
    }

    @Override
    public String getSourceName() {
        return "SpotifySourceManager";
    }

    @Override
    public AudioItem loadItem(DefaultAudioPlayerManager manager, AudioReference reference) {
        if (reference.identifier.startsWith("ytsearch:") || reference.identifier.startsWith("scsearch:")) return null;

        try {
            URL url = new URL(reference.identifier);
            if (!url.getHost().equalsIgnoreCase("open.spotify.com"))
                return null;
            String rawUrl = url.toString();

            AudioItem audioItem = null;
            if (TRACK_PATTERN.matcher(rawUrl).matches())
                audioItem = buildTrack(rawUrl);
            if (PLAYLIST_PATTERN.matcher(rawUrl).matches())
                audioItem = buildPlaylist(rawUrl);
            if (ALBUM_PATTERN.matcher(rawUrl).matches())
                audioItem = buildAlbum(rawUrl);
            if (TOPTEN_ARTIST_PATTERN.matcher(rawUrl).matches())
                audioItem = buildTopTenPlaylist(rawUrl);
            return audioItem;
        } catch (MalformedURLException e) {
            log.error("Failed to load the item!", e);
            return null;
        }
    }

    private AudioTrack buildTrack(String url) {
        String trackId = parseTrackPattern(url);

        JSONObject jsonTrack = getTrackById(trackId);
        TrackData trackData = getTrackData(Objects.requireNonNull(jsonTrack));
        return this.audioTrackFactory.getAudioTrack(trackData);
    }

    private AudioPlaylist buildPlaylist(String url) {
        PlaylistKey playlistKey = parsePlaylistPattern(url);

        JSONObject jsonPlaylist = getPlaylistById(playlistKey);
        PlaylistData playlistData = getPlaylistData(Objects.requireNonNull(jsonPlaylist));
        List<TrackData> trackDataList = playlistData.getTracks();
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist(playlistData.getName(), audioTracks, null, false);
    }

    private AudioPlaylist buildAlbum(String url) {
        AlbumKey albumKey = parseAlbumPattern(url);

        JSONObject jsonAlbum = getAlbumById(albumKey);
        AlbumData albumData = getAlbumData(Objects.requireNonNull(jsonAlbum));
        List<TrackData> trackDataList = albumData.getTracks();
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist(albumData.getName(), audioTracks, null, false);
    }

    private AudioPlaylist buildTopTenPlaylist(String url) {
        ArtistKey artistKey = parseArtistPattern(url);

        JSONObject jsonArtist = getArtistById(artistKey);
        ArtistData albumData = getArtistData(Objects.requireNonNull(jsonArtist));
        List<TrackData> trackDataList = albumData.getTracks();
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist(albumData.getName(), audioTracks, null, false);
    }

    private TrackData getTrackData(@NotNull JSONObject jsonObject) {
        JSONObject dataObject = jsonObject.has("data") ? jsonObject.getJSONObject("data") : jsonObject;
        String name = dataObject.getString("name"),
                url = dataObject.getString("url");
        List<String> artists = new ArrayList<>();
        dataObject.getJSONArray("artists").forEach(o -> {
            JSONObject jsonArtist = (JSONObject) o;
            artists.add(jsonArtist.getString("name"));
        });
        boolean local = dataObject.getBoolean("local"),
                explicit = dataObject.getBoolean("explicit");
        long duration = dataObject.getLong("durationTimeMillis");
        return new TrackData(
                name,
                url,
                artists,
                duration,
                local,
                explicit
        );
    }

    private PlaylistData getPlaylistData(@NotNull JSONObject jsonObject) {
        JSONObject dataObject = jsonObject.has("data") ? jsonObject.getJSONObject("data") : jsonObject;
        String name = dataObject.getString("name"),
                url = dataObject.getString("url"),
                owner = dataObject.getString("owner");
        List<TrackData> tracks = new ArrayList<>();
        dataObject.getJSONArray("tracks").forEach(o -> {
            JSONObject jsonTrack = (JSONObject) o;
            tracks.add(getTrackData(jsonTrack));
        });
        return new PlaylistData(
                name,
                url,
                owner,
                tracks
        );
    }

    private AlbumData getAlbumData(@NotNull JSONObject jsonObject) {
        JSONObject dataObject = jsonObject.has("data") ? jsonObject.getJSONObject("data") : jsonObject;
        String name = dataObject.getString("name"),
                url = dataObject.getString("url");
        List<String> artists = new ArrayList<>();
        dataObject.getJSONArray("artists").forEach(o -> {
            JSONObject jsonArtist = (JSONObject) o;
            artists.add(jsonArtist.getString("name"));
        });
        List<TrackData> tracks = new ArrayList<>();
        dataObject.getJSONArray("tracks").forEach(o -> {
            JSONObject jsonTrack = (JSONObject) o;
            tracks.add(getTrackData(jsonTrack));
        });
        return new AlbumData(
                name,
                artists,
                url,
                tracks
        );
    }

    private ArtistData getArtistData(@NotNull JSONObject jsonObject) {
        JSONObject dataObject = jsonObject.has("data") ? jsonObject.getJSONObject("data") : jsonObject;
        String name = dataObject.getString("name"),
                url = dataObject.getString("url");
        List<TrackData> tracks = new ArrayList<>();
        dataObject.getJSONArray("topTracks").forEach(o -> {
            JSONObject jsonTrack = (JSONObject) o;
            tracks.add(getTrackData(jsonTrack));
        });
        return new ArtistData(
                name,
                url,
                tracks
        );
    }

    private JSONObject getTrackById(String id) {
        JSONObject jsonObject = null;
        Request request = new Request.Builder()
                .url(SERVICE_BASE_URL + "/tracks/" + id)
                .get()
                .build();
        try (Response response = this.httpClient.newCall(request).execute()) {
            if (response.body() != null) {
                jsonObject = new JSONObject(response.body().string());
            }
        } catch (IOException e) {
            log.error("An error occurred while executing a GET request for looking up an track", e);
            return null;
        }
        return jsonObject;
    }

    private JSONObject getPlaylistById(PlaylistKey playlistKey) {
        JSONObject jsonObject = null;
        Request request = new Request.Builder()
                .url(SERVICE_BASE_URL + "/playlists/" + playlistKey.getPlaylistId())
                .get()
                .build();
        try (Response response = this.httpClient.newCall(request).execute()) {
            if (response.body() != null) {
                jsonObject = new JSONObject(response.body().string());
            }
        } catch (IOException e) {
            log.error("An error occurred while executing a GET request for looking up an playlist", e);
            return null;
        }
        return jsonObject;
    }

    private JSONObject getAlbumById(AlbumKey albumKey) {
        JSONObject jsonObject = null;
        Request request = new Request.Builder()
                .url(SERVICE_BASE_URL + "/albums/" + albumKey.getAlbumId())
                .get()
                .build();
        try (Response response = this.httpClient.newCall(request).execute()) {
            if (response.body() != null) {
                jsonObject = new JSONObject(response.body().string());
            }
        } catch (IOException e) {
            log.error("An error occurred while executing a GET request for looking up an album", e);
            return null;
        }
        return jsonObject;
    }

    private JSONObject getArtistById(ArtistKey artistKey) {
        JSONObject jsonObject = null;
        Request request = new Request.Builder()
                .url(SERVICE_BASE_URL + "/artists/" + artistKey.getArtistId())
                .get()
                .build();
        try (Response response = this.httpClient.newCall(request).execute()) {
            if (response.body() != null) {
                jsonObject = new JSONObject(response.body().string());
            }
        } catch (IOException e) {
            log.error("An error occurred while executing a GET request for looking up an artist", e);
            return null;
        }
        return jsonObject;
    }

    private String parseTrackPattern(String identifier) {
        final Matcher matcher = TRACK_PATTERN.matcher(identifier);
        if (!matcher.find())
            return "noTrackId";
        return matcher.group(1);
    }

    private PlaylistKey parsePlaylistPattern(String identifier) {
        final Matcher matcher = PLAYLIST_PATTERN.matcher(identifier);
        if (!matcher.find())
            return new PlaylistKey("noPlaylistId");
        return new PlaylistKey(matcher.group(1));
    }

    private AlbumKey parseAlbumPattern(String identifier) {
        final Matcher matcher = ALBUM_PATTERN.matcher(identifier);
        if (!matcher.find())
            return new AlbumKey("noAlbumId");
        String userId = matcher.group(1);
        return new AlbumKey(userId);
    }

    private ArtistKey parseArtistPattern(String identifier) {
        final Matcher matcher = TOPTEN_ARTIST_PATTERN.matcher(identifier);
        if (!matcher.find())
            return new ArtistKey("noArtistId");
        String userId = matcher.group(1);
        return new ArtistKey(userId);
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return false;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {
        throw new UnsupportedOperationException("encodeTrack is unsupported.");
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        throw new UnsupportedOperationException("decodeTrack is unsupported.");
    }

    @Override
    public void shutdown() {
    }
}
