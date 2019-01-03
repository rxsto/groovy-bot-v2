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

package co.groovybot.bot.core.audio.sources.spotify;

import co.groovybot.bot.core.audio.AudioTrackFactory;
import co.groovybot.bot.core.audio.data.TrackData;
import co.groovybot.bot.core.audio.sources.spotify.entities.keys.AlbumKey;
import co.groovybot.bot.core.audio.sources.spotify.entities.keys.ArtistKey;
import co.groovybot.bot.core.audio.sources.spotify.entities.keys.PlaylistKey;
import co.groovybot.bot.core.audio.sources.spotify.entities.keys.UserPlaylistKey;
import co.groovybot.bot.core.audio.sources.spotify.manager.SpotifyManager;
import co.groovybot.bot.core.audio.sources.spotify.request.GetNormalPlaylistRequest;
import co.groovybot.bot.core.audio.sources.spotify.request.GetNormalPlaylistTracksRequest;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.neovisionaries.i18n.CountryCode;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.albums.GetAlbumsTracksRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
public class SpotifySourceManager implements AudioSourceManager {

    private static final Pattern USER_PLAYLIST_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/user/(.*)/playlists?/([^?/\\s]*)");
    private static final Pattern PLAYLIST_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/playlists?/([^?/\\s]*)");
    private static final Pattern TRACK_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/tracks?/([^?/\\s]*)");
    private static final Pattern ALBUM_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/albums?/([^?/\\s]*)");
    private static final Pattern TOPTEN_ARTIST_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/artists?/([^?/\\s]*)");

    @Getter
    private final SpotifyManager spotifyManager;
    @Getter
    private final AudioTrackFactory audioTrackFactory;

    @Getter
    private LoadingCache<String, Track> trackLoadingCache;
    @Getter
    private LoadingCache<PlaylistKey, Playlist> playlistLoadingCache;
    @Getter
    private LoadingCache<UserPlaylistKey, Playlist> userPlaylistLoadingCache;
    @Getter
    private LoadingCache<ArtistKey, Playlist> topTenPlaylistLoadingCache;
    @Getter
    private LoadingCache<AlbumKey, Album> albumLoadingCache;

    @Getter
    private String retryAfter;

    public SpotifySourceManager(@NonNull SpotifyManager spotifyManager) {
        this.spotifyManager = spotifyManager;
        this.audioTrackFactory = new AudioTrackFactory();
        this.trackLoadingCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(12, TimeUnit.HOURS)
                .build(CacheLoader.asyncReloading(new CacheLoader<String, Track>() {
                    @Override
                    public Track load(@NotNull String key) {
                        return getTrackById(key);
                    }
                }, Executors.newCachedThreadPool()));
        this.playlistLoadingCache = CacheBuilder.newBuilder()
                .maximumSize(30)
                .expireAfterWrite(12, TimeUnit.HOURS)
                .build(CacheLoader.asyncReloading(new CacheLoader<PlaylistKey, Playlist>() {
                    @Override
                    public Playlist load(@NotNull PlaylistKey key) {
                        return getPlaylistById(key);
                    }
                }, Executors.newCachedThreadPool()));
        this.userPlaylistLoadingCache = CacheBuilder.newBuilder()
                .maximumSize(30)
                .expireAfterWrite(12, TimeUnit.HOURS)
                .build(CacheLoader.asyncReloading(new CacheLoader<UserPlaylistKey, Playlist>() {
                    @Override
                    public Playlist load(@NotNull UserPlaylistKey key) {
                        return getUserPlaylistById(key);
                    }
                }, Executors.newCachedThreadPool()));
        this.topTenPlaylistLoadingCache = CacheBuilder.newBuilder()
                .maximumSize(25)
                .expireAfterWrite(12, TimeUnit.HOURS)
                .build(CacheLoader.asyncReloading(new CacheLoader<ArtistKey, Playlist>() {
                    @Override
                    public Playlist load(@NotNull ArtistKey key) throws IOException, SpotifyWebApiException {
                        Artist artist = spotifyManager.getSpotifyApi().getArtist(key.getArtistId()).build().execute();
                        return convertListToPlaylist(getTopTenSongs(artist));
                    }
                }, Executors.newCachedThreadPool()));
        this.albumLoadingCache = CacheBuilder.newBuilder()
                .maximumSize(30)
                .expireAfterWrite(12, TimeUnit.HOURS)
                .build(CacheLoader.asyncReloading(new CacheLoader<AlbumKey, Album>() {
                    @Override
                    public Album load(@NotNull AlbumKey key) {
                        return getAlbumById(key);
                    }
                }, Executors.newCachedThreadPool()));
        this.retryAfter = "";
    }

    @Override
    public String getSourceName() {
        return "Spotify Source Manager";
    }

    @Override
    public AudioItem loadItem(DefaultAudioPlayerManager manager, AudioReference reference) {
        if (reference.identifier.startsWith("ytsearch:") || reference.identifier.startsWith("scsearch:")) return null;

        if (spotifyManager.getAccessTokenExpires() < System.currentTimeMillis()) {
            spotifyManager.refreshAccessToken();
            throw new FriendlyException("Our access-token is currently invalid. We are validating our authorization! Please try again in `30` seconds.", FriendlyException.Severity.COMMON, new Throwable());
        }

        try {
            URL url = new URL(reference.identifier);
            if (!url.getHost().equalsIgnoreCase("open.spotify.com"))
                return null;
            String rawUrl = url.toString();
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 429) {
                this.retryAfter = urlConnection.getHeaderField("Retry-After");
                throw new FriendlyException(String.format("We are currently rate-limited. Please try again in `%s`!", retryAfter), FriendlyException.Severity.COMMON, new Throwable());
            }

            AudioItem audioItem = null;

            if (TRACK_PATTERN.matcher(rawUrl).matches())
                audioItem = buildTrack(rawUrl);
            if (PLAYLIST_PATTERN.matcher(rawUrl).matches())
                audioItem = buildPlaylist(rawUrl);
            if (USER_PLAYLIST_PATTERN.matcher(rawUrl).matches())
                audioItem = buildUserPlaylist(rawUrl);
            if (ALBUM_PATTERN.matcher(rawUrl).matches())
                audioItem = buildPlaylistFromAlbum(rawUrl);
            if (TOPTEN_ARTIST_PATTERN.matcher(rawUrl).matches())
                audioItem = buildTopTenPlaylist(rawUrl);

            return audioItem;
        } catch (MalformedURLException e) {
            log.error("Failed to load the item!", e);
            return null;
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }

    private AudioTrack buildTrack(String url) {
        String trackId = parseTrackPattern(url);

        Track track;
        try {
            long startTime = System.currentTimeMillis();
            log.debug("Before: " + startTime);
            track = trackLoadingCache.get(trackId);
            log.debug("After: " + (System.currentTimeMillis() - startTime));
        } catch (ExecutionException e) {
            log.error(e);
            return null;
        }
        TrackData trackData = getTrackData(Objects.requireNonNull(track));
        return this.audioTrackFactory.getAudioTrack(trackData);
    }

    private AudioPlaylist buildPlaylist(String url) {
        PlaylistKey playlistKey = parsePlaylistPattern(url);

        Playlist playlist;
        try {
            long startTime = System.currentTimeMillis();
            log.debug("Before: " + startTime);
            playlist = playlistLoadingCache.get(playlistKey);
            log.debug("After: " + (System.currentTimeMillis() - startTime));
        } catch (ExecutionException e) {
            log.error(e);
            return null;
        }
        List<TrackData> trackDataList = this.getPlaylistTrackDataList(getPlaylistTracks(playlist));
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist(playlist.getName(), audioTracks, null, false);
    }

    private AudioPlaylist buildUserPlaylist(String url) {
        UserPlaylistKey userPlaylistKey = parseUserPlaylistPattern(url);

        this.spotifyManager.refreshAccessToken();
        GetPlaylistRequest getPlaylistRequest = this.spotifyManager.getSpotifyApi().getPlaylist(userPlaylistKey.getUserId(), userPlaylistKey.getPlaylistId())
                .build();
        Playlist playlist;
        try {
            long startTime = System.currentTimeMillis();
            log.debug("Before: " + startTime);
            playlist = getPlaylistRequest.execute();
            log.debug("After: " + (System.currentTimeMillis() - startTime));
        } catch (IOException | SpotifyWebApiException e) {
            log.error(e);
            return null;
        }
        List<TrackData> trackDataList = getPlaylistTrackDataList(Objects.requireNonNull(getPlaylistTracks(playlist)));
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist(playlist.getName(), audioTracks, null, false);
    }

    private AudioPlaylist buildPlaylistFromAlbum(String url) {
        AlbumKey albumKey = parseAlbumPattern(url);

        Album album;
        try {
            long startTime = System.currentTimeMillis();
            log.debug("Before: " + startTime);
            album = albumLoadingCache.get(albumKey);
            log.debug("After: " + (System.currentTimeMillis() - startTime));
        } catch (ExecutionException e) {
            log.error(e);
            return null;
        }
        List<TrackData> trackDataList = getTrackDataListSimplified(getAlbumTracks(Objects.requireNonNull(album)));
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist(album.getName(), audioTracks, null, false);
    }

    private AudioPlaylist buildTopTenPlaylist(String url) {
        ArtistKey artistKey = parseArtistPattern(url);

        this.spotifyManager.refreshAccessToken();
        GetArtistRequest getArtistRequest = this.spotifyManager.getSpotifyApi().getArtist(artistKey.getArtistId())
                .build();
        Artist artist;
        try {
            long startTime = System.currentTimeMillis();
            log.debug("Before: " + startTime);
            artist = getArtistRequest.execute();
            log.debug("After: " + (System.currentTimeMillis() - startTime));
        } catch (SpotifyWebApiException | IOException e) {
            log.error(e);
            return null;
        }
        List<TrackData> trackDataList = getTrackDataList(getTopTenSongs(artist));
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist("Top 10 Songs by " + artist.getName(), audioTracks, null, false);
    }

    private List<PlaylistTrack> getPlaylistTracks(Playlist playlist) {
        List<PlaylistTrack> playlistTracks = Lists.newArrayList();
        Paging<PlaylistTrack> currentPage = playlist.getTracks();

        int i = 0;
        do {
            playlistTracks.addAll(Arrays.asList(currentPage.getItems()));
            if (currentPage.getNext() == null)
                currentPage = null;
            else {
                try {
                    this.spotifyManager.refreshAccessToken();
                    URI nextPageUri = new URI(currentPage.getNext());
                    List<NameValuePair> queryPairs = URLEncodedUtils.parse(nextPageUri.toString(), StandardCharsets.UTF_8);
                    GetNormalPlaylistTracksRequest.Builder builder = new GetNormalPlaylistTracksRequest.Builder(this.spotifyManager.getAccessToken())
                            .playlistId(playlist.getId());
                    for (NameValuePair nameValuePair : queryPairs) {
                        builder = builder.setQueryParameter(nameValuePair.getName(), nameValuePair.getValue());
                    }

                    currentPage = builder.build().execute();
                    i++;
                } catch (URISyntaxException e) {
                    log.error("Got invalid 'next page' URI!", e);
                    return Collections.emptyList();
                } catch (SpotifyWebApiException | IOException e) {
                    log.error("Failed to query Spotify for playlist tracks!", e);
                    return Collections.emptyList();
                }
            }
        } while (currentPage != null);
        log.debug("UserPlaylist-Requests executed " + i + " times.");
        return playlistTracks;
    }

    private List<TrackSimplified> getAlbumTracks(Album album) {
        List<TrackSimplified> albumTracks = Lists.newArrayList();
        Paging<TrackSimplified> currentPage = album.getTracks();

        do {
            albumTracks.addAll(Arrays.asList(currentPage.getItems()));
            if (currentPage.getNext() == null)
                currentPage = null;
            else {
                try {
                    URI nextPageUri = new URI(currentPage.getNext());
                    List<NameValuePair> queryPairs = URLEncodedUtils.parse(nextPageUri.toString(), StandardCharsets.UTF_8);
                    GetAlbumsTracksRequest.Builder builder = this.spotifyManager.getSpotifyApi().getAlbumsTracks(album.getId());
                    for (NameValuePair nameValuePair : queryPairs) {
                        builder = builder.setQueryParameter(nameValuePair.getName(), nameValuePair.getValue());
                    }

                    currentPage = builder.build().execute();
                } catch (URISyntaxException e) {
                    log.error("Got invalid 'next page' URI!", e);
                } catch (SpotifyWebApiException | IOException e) {
                    log.error("Failed to query Spotify for album tracks!", e);
                }
            }
        } while (currentPage != null);
        return albumTracks;
    }

    private List<Track> getTopTenSongs(Artist artist) {
        List<Track> albumTracks = Lists.newArrayList();
        GetArtistsTopTracksRequest getArtistsTopTracksRequest = this.spotifyManager.getSpotifyApi().getArtistsTopTracks(artist.getId(), CountryCode.US)
                .build();
        try {
            albumTracks.addAll(Arrays.asList(getArtistsTopTracksRequest.execute()));
        } catch (IOException | SpotifyWebApiException e) {
            log.error("Failed to query top ten songs from artist!", e);
        }
        return albumTracks;
    }

    private List<TrackData> getPlaylistTrackDataList(@NotNull List<PlaylistTrack> playlistTracks) {
        return playlistTracks.stream()
                .map(PlaylistTrack::getTrack)
                .map(this::getTrackData)
                .collect(Collectors.toList());
    }

    private List<TrackData> getTrackDataListSimplified(@NotNull List<TrackSimplified> trackSimplifiedList) {
        return trackSimplifiedList.stream()
                .map(this::getTrackData)
                .collect(Collectors.toList());
    }

    private List<TrackData> getTrackDataList(@NotNull List<Track> tracks) {
        return tracks.stream()
                .map(this::getTrackData)
                .collect(Collectors.toList());
    }

    private TrackData getTrackData(@NotNull TrackSimplified trackSimplified) {
        return new TrackData(
                trackSimplified.getName(),
                trackSimplified.getExternalUrls().get("spotify"),
                Arrays.stream(trackSimplified.getArtists())
                        .map(ArtistSimplified::getName)
                        .collect(Collectors.toList()),
                trackSimplified.getDurationMs()
        );
    }

    private TrackData getTrackData(@NotNull Track track) {
        return new TrackData(
                track.getName(),
                track.getExternalUrls().get("spotify"),
                Arrays.stream(track.getArtists())
                        .map(ArtistSimplified::getName)
                        .collect(Collectors.toList()),
                track.getDurationMs()
        );
    }

    private Track getTrackById(String id) {
        this.spotifyManager.refreshAccessToken();
        try {
            return this.spotifyManager.getSpotifyApi()
                    .getTrack(id)
                    .build()
                    .execute();
        } catch (SpotifyWebApiException | IOException e) {
            log.error(e);
            return new Track.Builder().build();
        }
    }

    private Playlist getPlaylistById(PlaylistKey playlistKey) {
        this.spotifyManager.refreshAccessToken();
        GetNormalPlaylistRequest normalPlaylistRequest = new GetNormalPlaylistRequest.Builder(this.spotifyManager.getAccessToken())
                .playlistId(playlistKey.getPlaylistId())
                .build();
        try {
            return normalPlaylistRequest.execute();
        } catch (SpotifyWebApiException | IOException e) {
            log.error(e);
            return new Playlist.Builder().build();
        }
    }

    private Playlist getUserPlaylistById(UserPlaylistKey userPlaylistKey) {
        this.spotifyManager.refreshAccessToken();
        GetPlaylistRequest getPlaylistRequest = this.spotifyManager.getSpotifyApi().getPlaylist(userPlaylistKey.getUserId(), userPlaylistKey.getPlaylistId())
                .build();
        try {
            return getPlaylistRequest.execute();
        } catch (SpotifyWebApiException | IOException e) {
            log.error(e);
            return new Playlist.Builder().build();
        }
    }

    private Album getAlbumById(AlbumKey albumKey) {
        this.spotifyManager.refreshAccessToken();
        try {
            return this.spotifyManager.getSpotifyApi()
                    .getAlbum(albumKey.getAlbumId())
                    .build()
                    .execute();
        } catch (SpotifyWebApiException | IOException e) {
            log.error(e);
            return new Album.Builder().build();
        }
    }

    private Playlist convertListToPlaylist(List<Track> tracks) {
        List<PlaylistTrack> playlistTracks = Lists.newArrayList();
        tracks.forEach(track -> {
            PlaylistTrack playlistTrack = new PlaylistTrack.Builder()
                    .setTrack(track)
                    .setIsLocal(false)
                    .build();
            playlistTracks.add(playlistTrack);
        });
        Paging<PlaylistTrack> trackPaging = new Paging.Builder<PlaylistTrack>()
                .setTotal(playlistTracks.size())
                .setItems(playlistTracks.toArray(new PlaylistTrack[0]))
                .build();
        return new Playlist.Builder()
                .setTracks(trackPaging)
                .setId(tracks.get(0).getArtists()[0].getId())
                .build();
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

    private UserPlaylistKey parseUserPlaylistPattern(String identifier) {
        final Matcher matcher = USER_PLAYLIST_PATTERN.matcher(identifier);

        if (!matcher.find())
            return new UserPlaylistKey("noUserId", "noPlaylistId");
        String userId = matcher.group(1);
        String playlistId = matcher.group(2);
        return new UserPlaylistKey(userId, playlistId);
    }

    private AlbumKey parseAlbumPattern(String identifier) {
        final Matcher matcher = ALBUM_PATTERN.matcher(identifier);

        if (!matcher.find())
            return new AlbumKey("noUserId");
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
