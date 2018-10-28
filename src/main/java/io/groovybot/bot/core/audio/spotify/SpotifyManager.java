package io.groovybot.bot.core.audio.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class SpotifyManager {

    private static final Pattern PLAYLIST_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/user/(.*)/playlist/([^?/\\s]*)");
    private static final Pattern TRACK_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/track/([^?/\\s]*)");
    private final OkHttpClient httpClient;
    @Getter
    private final SpotifyApi spotifyApi;

    public SpotifyManager(String clientId, String clientToken) {
        this.httpClient = new OkHttpClient.Builder().build();
        this.spotifyApi = SpotifyApi.builder()
                .setAccessToken(getToken(Credentials.basic(clientId, clientToken)))
                .build();
    }

    private String getToken(String auth) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("grant_type", "client_credentials");
        Request.Builder requestBuilder = new Request.Builder()
                .post(formBody.build())
                .header("Authorization", auth)
                .url("https://accounts.spotify.com/api/token");
        try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
            if (response.body() != null) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                if (jsonObject.has("access_token"))
                    return jsonObject.getString("access_token");
            }
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    public Track getTrack(String url) {
        final String trackId = parseTrackPattern(url);
        if (trackId == null)
            return null;
        GetTrackRequest getTrackRequest = this.spotifyApi.getTrack(trackId)
                .build();
        Track track = null;
        try {
            track = getTrackRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            log.error(e);
        }
        return track;
    }

    public Playlist getPlaylist(String url) {
        String[] data = parsePlaylistPattern(url);
        if (data == null)
            return null;
        final String userId = data[0];
        final String playlistId = data[1];
        GetPlaylistRequest getPlaylistRequest = this.spotifyApi.getPlaylist(userId, playlistId)
                .build();
        Playlist playlist = null;
        try {
            playlist = getPlaylistRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            log.error(e);
        }
        return playlist;
    }

    private String parseTrackPattern(String identifier) {
        final Matcher matcher = TRACK_PATTERN.matcher(identifier);

        if (!matcher.find())
            return null;
        //returns the id of the track
        return matcher.group(1);
    }

    private String[] parsePlaylistPattern(String identifier) {
        String[] result = new String[2];
        final Matcher matcher = PLAYLIST_PATTERN.matcher(identifier);

        if (!matcher.find())
            return null;
        //saves the username
        result[0] = matcher.group(1);
        //saves the id of the playlist
        result[1] = matcher.group(2);
        return result;
    }
}
