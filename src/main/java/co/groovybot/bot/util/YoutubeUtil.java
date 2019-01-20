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

package co.groovybot.bot.util;


import co.groovybot.bot.GroovyBot;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequest;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Log4j2
public class YoutubeUtil {

    private final YouTube client;
    private final GroovyBot bot;

    private YoutubeUtil(GroovyBot bot) throws GeneralSecurityException, IOException {
        this.bot = bot;
        this.client = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                httpRequest -> {
                    // Do not so much
                })
                .setApplicationName("groovybot-discord")
                .setYouTubeRequestInitializer(new RequestInitializer())
                .build();
    }

    /**
     * Construcs a new Youtube util instance
     *
     * @param bot the current GroovyBot instance
     * @return a new YoutubeUtil instance
     */
    public static YoutubeUtil create(GroovyBot bot) {
        try {
            return new YoutubeUtil(bot);
        } catch (GeneralSecurityException | IOException e) {
            log.error("[YouTube] Error while establishing connection to YouTube", e);
        }
        return null;
    }

    /**
     * Retrieves the next video for the autoplay function
     *
     * @param videoId the ID of the prevoius video
     * @return The new videos SearchResult
     * @throws IOException          when an IO error occurred
     * @throws NullPointerException When no video where found
     */
    public SearchResult retrieveRelatedVideos(String videoId) throws IOException, NullPointerException {
        YouTube.Search.List search = client.search().list("id,snippet")
                .setRelatedToVideoId(videoId)
                .setType("video")
                .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
                .setMaxResults(1L);
        SearchListResponse searchResults = search.execute();
        if (searchResults.getItems().isEmpty())
            throw new NullPointerException("No videos were found");
        return searchResults.getItems().get(0);
    }

    public String getVideoId(String query) throws IOException {
        YouTube.Search.List search = client.search().list("id,snippet")
                .setType("video")
                .setFields("items(id/videoId)")
                .setQ(query);
        SearchListResponse response = search.execute();
        if (response.getItems().isEmpty())
            return "";
        return response.getItems().get(0).getId().getVideoId();
    }

    /**
     * Search for youtube Videos by it's ide
     *
     * @param videoId The id of the video
     * @return an VideoListResponse {@link com.google.api.services.youtube.model.VideoListResponse}
     * @throws IOException When YoutubeRequest returns an error
     */
    public VideoListResponse getVideoById(String videoId) throws IOException {
        return client.videos().list("snippet,localizations,contentDetails").setId(videoId).execute();
    }

    /**
     * Gets the first video from an VideoListResponse
     *
     * @param videoId The yotube video id
     * @return The first Video {@link com.google.api.services.youtube.model.Video} of the {@link com.google.api.services.youtube.model.VideoListResponse}
     * @throws IOException When YoutubeRequest returns an error
     * @see YoutubeUtil#getVideoById(String)
     */
    public Video getFirstVideoById(String videoId) throws IOException {
        return getVideoById(videoId).getItems().get(0);
    }

    private class RequestInitializer extends YouTubeRequestInitializer {
        @Override
        protected void initializeYouTubeRequest(YouTubeRequest<?> youTubeRequest) {
            youTubeRequest.setKey(bot.getConfig().getJSONObject("youtube").getString("apikey"));
        }
    }
}
