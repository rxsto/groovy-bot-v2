package io.groovybot.bot.core.audio.spotify;

import com.sedmelluq.discord.lavaplayer.track.*;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import io.groovybot.bot.core.audio.SearchResultHandler;
import io.groovybot.bot.core.audio.spotify.request.GetNormalPlaylistRequest;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class SpotifyManager {

    private static final Pattern USER_PLAYLIST_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/user/(.*)/playlist/([^?/\\s]*)");
    private static final Pattern PLAYLIST_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/playlist/([^?/\\s]*)");
    private static final Pattern TRACK_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/track/([^?/\\s]*)");
    private final OkHttpClient httpClient;
    private final String clientId, clientSecret;
    @Getter
    private SpotifyApi spotifyApi;
    private final String clientId, clientSecret;
    @Getter
    private final SpotifyPlaylistImporter playlistImporter;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private volatile long accessTokenExpires = 0;
    private volatile String accessToken = "";

    public SpotifyManager(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.httpClient = new OkHttpClient.Builder().build();

        refreshAccessToken();
        this.playlistImporter = new SpotifyPlaylistImporter(this, httpClient, accessToken);
        if (accessToken.isEmpty() || accessToken.equals(""))
            return;
        this.spotifyApi = SpotifyApi.builder()
                .setAccessToken(accessToken)
                .build();
    }

    public void refreshAccessToken() {
        if (System.currentTimeMillis() > this.accessTokenExpires) try {
            retrieveAccessToken();
        } catch (Exception e) {
            log.error("The access token could not be refreshed", e);
        }
    }

    private void retrieveAccessToken() {
        //checks if the clientId or the clientSecret are null or even the default value
        if ((this.clientId.isEmpty() || this.clientId.equals("defaultvalue"))
                || (this.clientSecret.isEmpty() || this.clientSecret.equals("defaultvalue"))) {
            log.info("The clientId or the clientSecret haven't been set correctly! Please configure your Spotify credentials, to use the Spotify api.");
            return;
        }

        //creating post request to Spotify api
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("grant_type", "client_credentials");
        Request.Builder requestBuilder = new Request.Builder()
                .post(formBody.build())
                .header("Authorization", Credentials.basic(this.clientId, this.clientSecret))
                .url("https://accounts.spotify.com/api/token");
        //executing post request
        try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
            if (response.body() != null) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                //checking if access token is available
                if (jsonObject.has("access_token")) {
                    //declaring the access token and the time after the access token expires
                    this.accessToken = jsonObject.getString("access_token");
                    this.accessTokenExpires = System.currentTimeMillis() + (jsonObject.getInt("expires_in") * 100);
                    log.debug("Received access token: " + accessToken + " which expires in: " + jsonObject.getInt("expires_in") + " seconds.");
                }
            }
        } catch (IOException e) {
            log.error("The access token couldn't be retrieved", e);
        }
    }

    public AudioPlaylist searchForTracks(String query, int timeout) {
        SearchResultHandler.PlaylistSearchException playlistSearchException = null;
        List<AudioTrack> trackList = new ArrayList<>();
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/search?q=" + query + "&type=track")
                .header("Authorization", "Bearer " + accessToken)
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            JSONObject jsonObject = new JSONObject(response.body().string());
            log.info(jsonObject.toString(2));
        } catch (IOException e) {
            log.error(e);
        }
        return new BasicAudioPlaylist("No searching results for " + query, Collections.emptyList(), null, true);
    }

    public Track getTrack(String url) {
        refreshAccessToken();
        final String trackId = parseTrackPattern(url);
        if (trackId == null)
            return null;
        GetTrackRequest getTrackRequest = this.spotifyApi.getTrack(trackId)
                .build();
        Track track = null;
        try {
            track = getTrackRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            log.error("The track could not be retrieved", e);
        }
        return track;
    }

    public Playlist getNormalPlaylist(String url) {
        refreshAccessToken(); //refreshing access token on every call of this method
        String playlistId = parsePlaylistPattern(url);
        GetNormalPlaylistRequest getNormalPlaylistRequest = new GetNormalPlaylistRequest.Builder(accessToken)
                .playlistId(Objects.requireNonNull(playlistId))
                .build();
        Playlist playlist = null;
        try {
            playlist = getNormalPlaylistRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            log.error("The playlist could not be retrieved", e);
        }
        return playlist;
    }

//    public Playlist getUserPlaylist(String url) {
//        refreshAccessToken();
//        String[] data = parseUserPlaylistPattern(url);
//        if (data == null)
//            return null;
//        final String userId = data[0];
//        final String playlistId = data[1];
//        GetPlaylistRequest getPlaylistRequest = this.spotifyApi.getPlaylist(userId, playlistId)
//                .build();
//        Playlist playlist = null;
//        try {
//            playlist = getPlaylistRequest.execute();
//        } catch (IOException | SpotifyWebApiException e) {
//            log.error("The playlist could not be retrieved", e);
//        }
//        return playlist;
//    }

    String parseTrackPattern(String identifier) {
        final Matcher matcher = TRACK_PATTERN.matcher(identifier);

        if (!matcher.find())
            return null;
        //returns the id of the track
        return matcher.group(1);
    }

    String parsePlaylistPattern(String identifier) {
        final Matcher matcher = PLAYLIST_PATTERN.matcher(identifier);

        if (!matcher.find())
            return null;
        //saves the username
        return matcher.group(1);
    }
}
