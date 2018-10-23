package io.groovybot.bot.core.lyrics;

import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;

@Log4j2
public class GeniusClient {

    private final OkHttpClient httpClient;
    private final String API_BASE = "https://api.genius.com";
    private final String GENIUS_BASE = "https://genius.com";

    public GeniusClient(String token) {
        this.httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request.Builder requestBuilder = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer " + token);
                    return chain.proceed(requestBuilder.build());
                }).build();
    }

    public String searchSong(String query) {
        Request request = new Request.Builder()
                .url(API_BASE + "/search?q=" + query)
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return GENIUS_BASE + new JSONObject(response.body().string()).getJSONObject("response").getJSONArray("hits").getJSONObject(0).getJSONObject("result").getString("path");
        } catch (IOException | JSONException e) {
            log.error("[Genius] An error occurred while getting song");
            return "";
        }
    }

}
