package io.groovybot.bot.core.audio.spotify.source;

import com.google.common.collect.Lists;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import io.groovybot.bot.core.audio.AudioTrackFactory;
import io.groovybot.bot.core.audio.spotify.SpotifyManager;
import io.groovybot.bot.core.audio.spotify.entities.PlaylistKey;
import io.groovybot.bot.core.audio.spotify.entities.TrackData;
import io.groovybot.bot.core.audio.spotify.entities.UserPlaylistKey;
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

    private static final Pattern USER_PLAYLIST_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/user/(.*)/playlist/([^?/\\s]*)");
    private static final Pattern PLAYLIST_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/playlists?/([^?/\\s]*)");
    private static final Pattern TRACK_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/track/([^?/\\s]*)");

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
        return "Spotify Playlist";
    }

    @Override
    public AudioItem loadItem(DefaultAudioPlayerManager manager, AudioReference reference) {
        try {
            URL url = new URL(reference.identifier);
            if (!url.getHost().equalsIgnoreCase("open.spotify.com"))
                return null;
            AudioItem audioItem = buildPlaylist(url.toString());
            if (audioItem == null) {
                audioItem = buildUserPlaylist(url.toString());
                if (audioItem == null)
                    audioItem = buildTrack(url.toString());
            }
            return audioItem;
        } catch (MalformedURLException e) {
            log.error(e);
        }
        return null;
    }

    private AudioTrack buildTrack(String url) {
        String trackId = parseTrackPattern(url);
        Track track;
        try {
            track = this.spotifyManager.getSpotifyApi()
                    .getTrack(trackId)
                    .build()
                    .execute();
        } catch (SpotifyWebApiException | IOException e) {
            log.error("Unable to fetch track with the given track id!", e);
            return null;
        }
        TrackData trackData = this.getTrackData(Objects.requireNonNull(track));
        return this.audioTrackFactory.getAudioTrack(trackData);
    }

    private AudioPlaylist buildPlaylist(String url) {
        PlaylistKey playlistKey = parsePlaylistPattern(url);
        GetNormalPlaylistRequest normalPlaylistRequest = new GetNormalPlaylistRequest.Builder(this.spotifyManager.getAccessToken())
                .playlistId(Objects.requireNonNull(playlistKey).getPlaylistId())
                .build();
        Playlist playlist;
        try {
            playlist = normalPlaylistRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            log.error("Unable to fetch playlist with the given playlist id!", e);
            return null;
        }
        List<TrackData> trackDataList = getTrackDataList(getPlaylistTracks(Objects.requireNonNull(playlist)));
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist(playlist.getName(), audioTracks, null, false);
    }

    private AudioPlaylist buildUserPlaylist(String url) {
        UserPlaylistKey userPlaylistKey = parseUserPlaylistPattern(url);
        GetPlaylistRequest getPlaylistRequest = this.spotifyManager.getSpotifyApi().getPlaylist(userPlaylistKey.getUserId(), userPlaylistKey.getPlaylistId())
                .build();
        Playlist playlist;
        try {
            playlist = getPlaylistRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            log.error("Unable to fetch user playlist with the given user id and playlist id!", e);
            return null;
        }
        List<TrackData> trackDataList = getTrackDataList(getPlaylistTracks(Objects.requireNonNull(playlist)));
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist(playlist.getName(), audioTracks, null, false);
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

    private List<TrackData> getTrackDataList(@NotNull List<PlaylistTrack> playlistTracks) {
        return playlistTracks.stream()
                .map(PlaylistTrack::getTrack)
                .map(this::getTrackData)
                .collect(Collectors.toList());
    }

    private TrackData getTrackData(@NotNull Track track) {
        return new TrackData(
                track.getName(),
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
        //saves the userId
        String userId = matcher.group(1);
        //saves the playlistId
        String playlistId = matcher.group(2);
        return new UserPlaylistKey(userId, playlistId);
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
