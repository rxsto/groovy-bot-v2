package io.groovybot.bot.core.audio.spotify.outdated;

import io.groovybot.bot.core.audio.spotify.SpotifyManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;

@Log4j2
public class SpotifyPlaylistImporter {

    private final SpotifyManager spotifyManager;
    @Getter
    private OkHttpClient httpClient;
    private String accessToken;

    public SpotifyPlaylistImporter(SpotifyManager spotifyManager, OkHttpClient httpClient, String accessToken) {
        this.spotifyManager = spotifyManager;
        this.httpClient = httpClient;
        this.accessToken = accessToken;
    }

    /*public SpotifyPlaylistInfo getPlaylistInfo(String url) {
        spotifyManager.refreshAccessToken();
        String playlistId = spotifyManager.parsePlaylistPattern(url);
        String playlistName = "Spotify Playlist";
        int tracks = 0;

        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/playlists" + playlistId)
                .header("Authorization", "Bearer " + accessToken)
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            JSONObject playlistInfo = new JSONObject(response.body().string());
            playlistName = playlistInfo.getString("name");
            tracks = playlistInfo.getInt("total");
        } catch (IOException e) {
            log.error(e);
        }
        return new SpotifyPlaylistInfo(playlistName, tracks);
    }

    public List<String> getPlaylistItems(String url) {
        spotifyManager.refreshAccessToken();
        String playlistId = spotifyManager.parsePlaylistPattern(url);
        JSONObject jsonPage = null;
        List<String> playlistItems = new ArrayList<>();

        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/playlists/" + playlistId + "/tracks")
                .header("Authorization", "Bearer " + accessToken)
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            jsonPage = new JSONObject(response.body().string());
        } catch (IOException e) {
            log.error(e);
        }

        JSONArray jsonTracks = Objects.requireNonNull(jsonPage).getJSONArray("items");
        jsonTracks.forEach(jsonTrack -> {
            try {
                JSONObject track = ((JSONObject) jsonTrack).getJSONObject("track");
                final StringBuilder stringBuilder = new StringBuilder();
                track.getJSONArray("artists").forEach(jsonArtist ->
                        stringBuilder.append(((JSONObject) jsonArtist).getString("name")).append(" - "));
                stringBuilder.append(track.getString("name"));

                playlistItems.add(stringBuilder.toString());
                log.debug("Playlist Item: " + stringBuilder.toString());
            } catch (Exception e) {
                log.error("Could not receive items from playlist", e);
            }
        });
        return playlistItems;
    }*/

    public static class SpotifyPlaylistInfo {

        @Getter
        @Setter
        private String playlistName;
        @Getter
        private int tracks;

        public SpotifyPlaylistInfo(String playlistName, int tracks) {
            this.playlistName = playlistName;
            this.tracks = tracks;
        }
    }
}
