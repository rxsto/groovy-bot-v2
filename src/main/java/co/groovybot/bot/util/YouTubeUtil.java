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
import com.google.api.services.youtube.model.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class YouTubeUtil {

    private final YouTube client;
    private final GroovyBot bot;

    private static final String PROTOCOL_REGEX = "(https?://)?";
    private static final String YOUTUBE_BASE_URL = PROTOCOL_REGEX + "((?:www\\.|m\\.|music\\.|)youtube\\.com/.*|(?:www\\.|)youtu\\.be/.*)";
//    private static final Pattern PLAYLIST_ID_REGEX = Pattern.compile(YOUTUBE_BASE_URL + "(?<list>(PL|LL|FL|UU)[a-zA-Z0-9_-]+)");
    private static final Pattern PLAYLIST_ID_REGEX = Pattern.compile(YOUTUBE_BASE_URL + "(?<list>(PL|LL|FL|UU)[a-zA-Z0-9_-]+)");
    private static final Pattern VIDEO_ID_REGEX = Pattern.compile(YOUTUBE_BASE_URL + "(?<v>[a-zA-Z0-9_-]{11})");

    private YouTubeUtil(GroovyBot bot) throws GeneralSecurityException, IOException {
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
     * Constructs a new YouTube util instance
     *
     * @param bot the current GroovyBot instance
     * @return a new YouTubeUtil instance
     */
    public static YouTubeUtil create(GroovyBot bot) {
        try {
            return new YouTubeUtil(bot);
        } catch (GeneralSecurityException | IOException e) {
            log.error("[YouTube] Error while establishing connection to YouTube", e);
        }
        return null;
    }

    /**
     * Retrieves the next video for the autoplay feature
     *
     * @param videoId the id of the previous video
     * @return a SearchResult with the related videos
     * @throws IOException          if an IO error occurred
     * @throws NullPointerException if no video where found
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
     * Search for YouTube videos by it's id
     *
     * @param videoId the id of the video
     * @return an VideoListResponse {@link com.google.api.services.youtube.model.VideoListResponse}
     * @throws IOException if YouTubeRequest returns an error
     */
    public VideoListResponse getVideoById(String videoId) throws IOException {
        return client.videos().list("snippet,localizations,contentDetails").setId(videoId).execute();
    }

    /**
     * Gets the first video from an VideoListResponse
     *
     * @param videoId the YouTube video id
     * @return the first Video {@link com.google.api.services.youtube.model.Video} of the {@link com.google.api.services.youtube.model.VideoListResponse}
     * @throws IOException if YouTubeRequest returns an error
     * @see YouTubeUtil#getVideoById(String)
     */
    public Video getFirstVideoById(String videoId) throws IOException {
        return getVideoById(videoId).getItems().get(0);
    }

    public List<String> getVideoIdsFromPlaylist(String playlistUrl) throws IOException {
        List<String> idList = new ArrayList<>();
        if (PLAYLIST_ID_REGEX.matcher(playlistUrl).matches()) {
            String playlistId = parsePlaylistUrl(playlistUrl);
            YouTube.PlaylistItems.List list = client.playlistItems().list("id,snippet").setFields("items(snippet/resourceId/videoId)").setPlaylistId(playlistId);
            PlaylistItemListResponse playlistItemListResponse = list.execute();
            playlistItemListResponse.getItems().forEach(playlistItem -> idList.add(playlistItem.getSnippet().getResourceId().getVideoId()));

            System.out.println(Arrays.toString(idList.toArray()));
        }
        return idList;
    }

    private String parsePlaylistUrl(String playlistUrl) {
        final Matcher matcher = PLAYLIST_ID_REGEX.matcher(playlistUrl);
        if (!matcher.find())
            return "";
        return matcher.group("list");
    }

    private class RequestInitializer extends YouTubeRequestInitializer {
        @Override
        protected void initializeYouTubeRequest(YouTubeRequest<?> youTubeRequest) {
            youTubeRequest.setKey(bot.getConfig().getJSONObject("youtube").getString("apikey"));
        }
    }
}
