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

package co.groovybot.bot.core.lyrics;

import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

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
            assert response.body() != null;
            return GENIUS_BASE + new JSONObject(response.body().string()).getJSONObject("response").getJSONArray("hits").getJSONObject(0).getJSONObject("result").getString("path");
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    public String getLyrics(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            document.select("br").append("\\n");
            document.select("p").prepend("\\n\\n");
            Element element = document.selectFirst(".lyrics");
            if (!element.hasText()) return "Something went wrong while fetching lyrics ...";
            return Jsoup.clean(element.html(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false)).replace("\\n", "\n");
        } catch (IOException e) {
            log.error("[GeniusClient] An error occurred while getting lyrics!", e);
            return null;
        }
    }

    public String getTitle(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.title().split(" \\| Genius Lyrics")[0];
        } catch (IOException e) {
            log.error("[GeniusClient] An error occurred while getting lyrics!", e);
            return null;
        }
    }
}
