package io.groovybot.bot.core.audio;


import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.User;

@RequiredArgsConstructor
public class QueuedTrack implements AudioTrack {

    private final AudioTrack track;
    @Getter
    private final User requester;

    @Override
    public AudioTrackInfo getInfo() {
        return track.getInfo();
    }

    @Override
    public String getIdentifier() {
        return track.getIdentifier();
    }

    @Override
    public AudioTrackState getState() {
        return track.getState();
    }

    @Override
    public void stop() {
        track.stop();
    }

    @Override
    public boolean isSeekable() {
        return track.isSeekable();
    }

    @Override
    public long getPosition() {
        return track.getPosition();
    }

    @Override
    public void setPosition(long position) {
        track.setPosition(position);
    }

    @Override
    public void setMarker(TrackMarker marker) {
        track.setMarker(marker);
    }

    @Override
    public long getDuration() {
        return track.getDuration();
    }

    @Override
    public AudioTrack makeClone() {
        return track.makeClone();
    }

    @Override
    public AudioSourceManager getSourceManager() {
        return track.getSourceManager();
    }

    @Override
    public void setUserData(Object userData) {
        track.setUserData(userData);
    }

    @Override
    public Object getUserData() {
        return track.getUserData();
    }

    @Override
    public <T> T getUserData(Class<T> clazz) {
        return track.getUserData(clazz);
    }

}
