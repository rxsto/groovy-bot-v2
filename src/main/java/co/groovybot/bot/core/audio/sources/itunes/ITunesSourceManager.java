package co.groovybot.bot.core.audio.sources.itunes;

import be.ceau.itunesapi.Lookup;
import be.ceau.itunesapi.request.Entity;
import be.ceau.itunesapi.response.Response;
import be.ceau.itunesapi.response.Result;
import co.groovybot.bot.core.audio.AudioTrackFactory;
import co.groovybot.bot.core.audio.data.TrackData;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
public class ITunesSourceManager implements AudioSourceManager {

    private static final Pattern ALBUM_PATTERN = Pattern.compile("https?://itunes\\.apple\\.com/.*/album/.*/([0-9]*)");

    @Getter
    private AudioTrackFactory audioTrackFactory;

    public ITunesSourceManager() {
        this.audioTrackFactory = new AudioTrackFactory();
    }

    @Override
    public String getSourceName() {
        return "iTunesSourceManager";
    }

    @Override
    public AudioItem loadItem(DefaultAudioPlayerManager manager, AudioReference reference) {
        if (reference.identifier.startsWith("ytsearch:") || reference.identifier.startsWith("scsearch:")) return null;
        try {
            URL url = new URL(reference.identifier);
            if (!url.getHost().equalsIgnoreCase("itunes.apple.com"))
                return null;
            String rawUrl = url.toString();
            AudioItem audioItem = null;

            if (ALBUM_PATTERN.matcher(rawUrl).matches())
                audioItem = buildAlbum(rawUrl);
            return audioItem;
        } catch (MalformedURLException e) {
            log.error("Failed to load the item!", e);
            return null;
        }
    }

    private AudioPlaylist buildAlbum(String url) {
        String albumId = parseAlbumPattern(url);
        Response response = new Lookup()
                .addId(albumId)
                .setEntity(Entity.SONG)
                .execute();
        List<Result> results = response.getResults().stream()
                .filter(result -> result.getWrapperType().equals("track"))
                .collect(Collectors.toList());
        List<TrackData> trackDataList = getPlaylistTrackData(results);
        List<AudioTrack> audioTracks = this.audioTrackFactory.getAudioTracks(trackDataList);
        return new BasicAudioPlaylist(results.get(0).getCollectionName(), audioTracks, null, false);
    }

    private List<TrackData> getPlaylistTrackData(List<Result> playlistTracks) {
        return playlistTracks.stream()
                .map(this::getTrackData)
                .collect(Collectors.toList());
    }

    private TrackData getTrackData(Result result) {
        return new TrackData(
                result.getTrackName(),
                result.getFeedUrl(),
                Collections.singletonList(result.getArtistName()),
                result.getTrackTimeMillis()
        );
    }

    private String parseAlbumPattern(String identifier) {
        final Matcher matcher = ALBUM_PATTERN.matcher(identifier);

        if (!matcher.find())
            return "noAlbumId";
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
