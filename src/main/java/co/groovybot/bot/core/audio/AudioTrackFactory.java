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

package co.groovybot.bot.core.audio;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.data.TrackData;
import co.groovybot.bot.util.YoutubeUtil;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
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
            String identifier = GroovyBot.getInstance().getYoutubeClient().getVideoId(trackData.getArtists().get(0) + " " +
                    trackData.getTitle());
            AudioTrackInfo audioTrackInfo = new AudioTrackInfo(
                    trackData.getTitle(),
                    trackData.getArtists().get(0),
                    trackData.getDuration(),
                    identifier, false,
                    trackData.getUrl()
            );
            return new YoutubeAudioTrack(audioTrackInfo, new YoutubeAudioSourceManager());
        } catch (IOException e) {
            log.error("[AudioTrackFactory] Failed to convert TrackData to AudioTrack!", e);
            return null;
        }
    }
}
