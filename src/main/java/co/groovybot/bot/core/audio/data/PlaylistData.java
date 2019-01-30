package co.groovybot.bot.core.audio.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PlaylistData {

    private final String name;
    private final String url;
    private final String owner;
    private final List<TrackData> tracks;
}
