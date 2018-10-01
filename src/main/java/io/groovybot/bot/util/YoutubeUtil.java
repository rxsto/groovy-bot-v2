package io.groovybot.bot.util;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequest;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.GroovyBot;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Log4j
public class YoutubeUtil {

    private final YouTube client;
    private final GroovyBot bot;

    public static YoutubeUtil create(GroovyBot bot) {
        try {
            return new YoutubeUtil(bot);
        } catch (GeneralSecurityException | IOException e) {
            log.error("[YouTube] Error while establishing connection to YouTube", e);
        }
        return null;
    }

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

    private class RequestInitializer extends YouTubeRequestInitializer {
        @Override
        protected void initializeYouTubeRequest(YouTubeRequest<?> youTubeRequest) {
            youTubeRequest.setKey(bot.getConfig().getJSONObject("youtube").getString("apikey"));
        }
    }

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

}
