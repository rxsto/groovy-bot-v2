package co.groovybot.bot.core.audio.sources.itunes;

import be.ceau.itunesapi.Lookup;
import be.ceau.itunesapi.request.Entity;
import be.ceau.itunesapi.response.Response;
import co.groovybot.bot.core.audio.AudioTrackFactory;
import co.groovybot.bot.core.audio.data.TrackData;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class ITunesSourceManager implements AudioSourceManager {

    private static final Pattern TRACK_PATTERN = Pattern.compile("a");
    private static final Pattern PLAYLIST_PATTERN = Pattern.compile("a");

    @Getter
    private AudioTrackFactory audioTrackFactory;

    public ITunesSourceManager() {
        this.audioTrackFactory = new AudioTrackFactory();
    }

    @Override
    public String getSourceName() {
        return "iTunes Source Manager";
    }

    @Override
    public AudioItem loadItem(DefaultAudioPlayerManager manager, AudioReference reference) {
        if (reference.identifier.startsWith("ytsearch:") || reference.identifier.startsWith("scsearch:")) return null;
        try {
            URL url = new URL(reference.identifier);
            if (!url.getHost().equalsIgnoreCase("itunes"))
                return null;
            String rawUrl = url.toString();
            AudioItem audioItem = null;

            if (TRACK_PATTERN.matcher(rawUrl).matches())
                audioItem = buildTrack(rawUrl);
//            if (PLAYLIST_PATTERN.matcher(rawUrl).matches())
//                audioItem = buildPlaylist(rawUrl);
            return audioItem;
        } catch (MalformedURLException e) {
            log.error("Failed to load the item!", e);
            return null;
        }
    }

    private AudioTrack buildTrack(String url) {
        String trackId = parseTrackPattern(url);
        Response response = new Lookup()
                .addId("")
                .setEntity(Entity.ALL_TRACK)
                .execute();
        TrackData trackData = null;
        return this.audioTrackFactory.getAudioTrack(trackData);
    }

    private String parseTrackPattern(String identifier) {
        final Matcher matcher = TRACK_PATTERN.matcher(identifier);

        if (!matcher.find())
            return "noTrackId";
        return matcher.group(1);
    }

    private String parsePlaylistPattern(String identifier) {
        final Matcher matcher = PLAYLIST_PATTERN.matcher(identifier);

        if (!matcher.find())
            return "noPlaylistId";
        return matcher.group(1);
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return false;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {
        throw new UnsupportedOperationException("encodeTrack is unsupported.");
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        throw new UnsupportedOperationException("decodeTrack is unsupported.");
    }

    @Override
    public void shutdown() {
    }
}
