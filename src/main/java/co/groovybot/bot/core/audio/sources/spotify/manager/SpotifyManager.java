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

package co.groovybot.bot.core.audio.sources.spotify.manager;

import com.wrapper.spotify.SpotifyApi;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

@Log4j2
public class SpotifyManager {

    private final OkHttpClient httpClient;
    private final String clientId, clientSecret;
    @Getter
    private SpotifyApi spotifyApi;
    @Getter
    private volatile long accessTokenExpires = 0;
    @Getter
    private volatile String accessToken = "";

    public SpotifyManager(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.httpClient = new OkHttpClient.Builder().build();

        refreshAccessToken();
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
            log.error("[SpotifyManager] The access token could not be refreshed!", e);
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
                    accessToken = jsonObject.getString("access_token");
                    spotifyApi = SpotifyApi.builder().setAccessToken(accessToken).build();
                    accessTokenExpires = System.currentTimeMillis() + (jsonObject.getInt("expires_in") * 1000);
                    log.debug("Received access token: * which expires in: {} seconds.", jsonObject.getInt("expires_in"));
                }
            }
        } catch (IOException e) {
            log.error("The access token couldn't be retrieved", e);
        }
    }
}
