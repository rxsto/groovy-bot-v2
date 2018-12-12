package io.groovybot.bot.core.audio.spotify.source;

import com.google.common.collect.Lists;
import com.neovisionaries.i18n.CountryCode;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.albums.GetAlbumRequest;
import com.wrapper.spotify.requests.data.albums.GetAlbumsTracksRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import io.groovybot.bot.core.audio.AudioTrackFactory;
import io.groovybot.bot.core.audio.spotify.SpotifyManager;
import io.groovybot.bot.core.audio.spotify.entities.AlbumKey;
import io.groovybot.bot.core.audio.spotify.entities.ArtistKey;
import io.groovybot.bot.core.audio.spotify.entities.PlaylistKey;
import io.groovybot.bot.core.audio.spotify.entities.UserPlaylistKey;
import io.groovybot.bot.core.audio.spotify.entities.track.TrackData;
import io.groovybot.bot.core.audio.spotify.request.GetNormalPlaylistRequest;
import io.groovybot.bot.core.audio.spotify.request.GetNormalPlaylistsTracksRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    public SpotifySourceManager(@NonNull SpotifyManager spotifyManager, @NonNull AudioTrackFactory audioTrackFactory) {
        this.spotifyManager = spotifyManager;
        this.audioTrackFactory = audioTrackFactory;
    }

    @Override
    public String getSourceName() {
        return "Spotify Source Manager";
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
        }
    }

    private AudioTrack buildTrack(String url) {
        this.spotifyManager.refreshAccessToken();
        String trackId = parseTrackPattern(url);
        Track track;
        try {
            track = this.spotifyManager.getSpotifyApi()
                    .getTrack(trackId)
                    .build()
                    .execute();
        } catch (SpotifyWebApiException | IOException e) {
            return null;
        }
        TrackData trackData = this.getTrackData(Objects.requireNonNull(track));
        return this.audioTrackFactory.getAudioTrack(trackData);
    }

    private AudioPlaylist buildPlaylist(String url) {
        this.spotifyManager.refreshAccessToken();
        PlaylistKey playlistKey = parsePlaylistPattern(url);
        GetNormalPlaylistRequest normalPlaylistRequest = new GetNormalPlaylistRequest.Builder(this.spotifyManager.getAccessToken())
                .playlistId(Objects.requireNonNull(playlistKey).getPlaylistId())
                .build();
        Playlist playlist;
        try {
            playlist = normalPlaylistRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            return null;
        }
        List<TrackData> trackDataList = getPlaylistTrackDataList(getPlaylistTracks(Objects.requireNonNull(playlist)));
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist(playlist.getName(), audioTracks, null, false);
    }

    private AudioPlaylist buildUserPlaylist(String url) {
        this.spotifyManager.refreshAccessToken();
        UserPlaylistKey userPlaylistKey = parseUserPlaylistPattern(url);
        GetPlaylistRequest getPlaylistRequest = this.spotifyManager.getSpotifyApi().getPlaylist(userPlaylistKey.getUserId(), userPlaylistKey.getPlaylistId())
                .build();
        Playlist playlist;
        try {
            playlist = getPlaylistRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            return null;
        }
        List<TrackData> trackDataList = getPlaylistTrackDataList(getPlaylistTracks(Objects.requireNonNull(playlist)));
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist(playlist.getName(), audioTracks, null, false);
    }

    private AudioPlaylist buildPlaylistFromAlbum(String url) {
        this.spotifyManager.refreshAccessToken();
        AlbumKey albumKey = parseAlbumPattern(url);
        GetAlbumRequest getAlbumRequest = this.spotifyManager.getSpotifyApi().getAlbum(albumKey.getAlbumId())
                .build();
        Album album;
        try {
            album = getAlbumRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            return null;
        }
        List<TrackData> trackDataList = getTrackDataListSimplified(getAlbumTracks(Objects.requireNonNull(album)));
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist(album.getName(), audioTracks, null, false);
    }

    private AudioPlaylist buildTopTenPlaylist(String url) {
        this.spotifyManager.refreshAccessToken();
        ArtistKey artistKey = parseArtistPattern(url);
        GetArtistRequest getArtistRequest = this.spotifyManager.getSpotifyApi().getArtist(artistKey.getArtistId())
                .build();
        Artist artist;
        try {
            artist = getArtistRequest.execute();
        } catch (SpotifyWebApiException | IOException e) {
            return null;
        }
        List<TrackData> trackDataList = getTrackDataList(getTopTenSongs(artist));
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist("Top 10 Songs by " + artist.getName(), audioTracks, null, false);
    }

    private List<PlaylistTrack> getPlaylistTracks(Playlist playlist) {
        List<PlaylistTrack> playlistTracks = Lists.newArrayList();
        Paging<PlaylistTrack> currentPage = playlist.getTracks();

        do {
            playlistTracks.addAll(Arrays.asList(currentPage.getItems()));
            if (currentPage.getNext() == null)
                currentPage = null;
            else {
                try {
                    URI nextPageUri = new URI(currentPage.getNext());
                    List<NameValuePair> queryPairs = URLEncodedUtils.parse(nextPageUri, StandardCharsets.UTF_8);
                    GetNormalPlaylistsTracksRequest.Builder builder = new GetNormalPlaylistsTracksRequest.Builder(this.spotifyManager.getAccessToken())
                            .playlistId(playlist.getId());
                    for (NameValuePair nameValuePair : queryPairs) {
                        builder = builder.setQueryParameter(nameValuePair.getName(), nameValuePair.getValue());
                    }

                    currentPage = builder.build().execute();
                } catch (URISyntaxException e) {
                    log.error("Got invalid 'next page' URI!", e);
                } catch (SpotifyWebApiException | IOException e) {
                    log.error("Failed to query Spotify for playlist tracks!", e);
                }
            }
        } while (currentPage != null);
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
                    List<NameValuePair> queryPairs = URLEncodedUtils.parse(nextPageUri, StandardCharsets.UTF_8);
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
