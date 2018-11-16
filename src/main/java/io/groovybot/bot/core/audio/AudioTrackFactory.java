package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.audio.spotify.entities.TrackData;
import io.groovybot.bot.util.YoutubeUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AudioTrackFactory {

    private final YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager();

    public List<AudioTrack> getAudioTracks(List<TrackData> trackDataList) {
        return trackDataList.stream()
                .map(this::getAudioTrack)
                .collect(Collectors.toList());
    }

    public AudioTrack getAudioTrack(TrackData trackData) {
        try {
            String identifier = Objects.requireNonNull(YoutubeUtil.create(GroovyBot.getInstance())).getVideoId(trackData.getArtists().get(0) + " " + trackData.getTitle());
            AudioTrackInfo audioTrackInfo = new AudioTrackInfo(trackData.getTitle(), trackData.getArtists().get(0), trackData.getDuration(), identifier, false, "");
            return new YoutubeAudioTrack(audioTrackInfo, youtubeAudioSourceManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
