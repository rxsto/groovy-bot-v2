package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.audio.spotify.entities.track.TrackData;
import io.groovybot.bot.util.YoutubeUtil;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
public class AudioTrackFactory {

    public List<AudioTrack> getAudioTracks(List<TrackData> trackDataList) {
        return trackDataList.stream()
                .map(this::getAudioTrack)
                .collect(Collectors.toList());
    }

    public AudioTrack getAudioTrack(TrackData trackData) {
        try {
            String identifier = Objects.requireNonNull(YoutubeUtil.create(GroovyBot.getInstance())).getVideoId(trackData.getArtists().get(0) + " " + trackData.getTitle());
            AudioTrackInfo audioTrackInfo = new AudioTrackInfo(trackData.getTitle(), trackData.getArtists().get(0), trackData.getDuration(), identifier, false, trackData.getUri());
            return new YoutubeAudioTrack(audioTrackInfo, new YoutubeAudioSourceManager());
        } catch (IOException e) {
            log.error("[AudioTrackFactory] Failed to convert TrackData to AudioTrack!", e);
        }
        return null;
    }
}
