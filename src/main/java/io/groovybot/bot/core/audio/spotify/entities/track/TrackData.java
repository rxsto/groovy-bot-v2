package io.groovybot.bot.core.audio.spotify.entities.track;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TrackData {

    private final String title;
    private final String uri;
    private final List<String> artists;
    private final long duration;
}
