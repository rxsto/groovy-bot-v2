package co.groovybot.bot.core.audio.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ArtistData {

    private final String name;
    private final String url;
    private final List<TrackData> tracks;
}
