package io.groovybot.bot.util;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequest;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import io.groovybot.bot.GroovyBot;
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
            throw new NullPointerException("No videos were found");
        return response.getItems().get(0).getId().getVideoId();
    }

    private class RequestInitializer extends YouTubeRequestInitializer {
        @Override
        protected void initializeYouTubeRequest(YouTubeRequest<?> youTubeRequest) {
            youTubeRequest.setKey(bot.getConfig().getJSONObject("youtube").getString("apikey"));
        }
    }
}
