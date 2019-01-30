package co.groovybot.bot.core.audio.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class AlbumData {

    private final String name;
    private final List<String> artists;
    private final String url;
    private final List<TrackData> tracks;
}
