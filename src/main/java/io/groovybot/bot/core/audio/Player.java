package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.IPlayer;
import lavalink.client.player.event.AudioEventAdapterWrapped;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class Player extends AudioEventAdapterWrapped {

    protected Queue<AudioTrack> trackQueue;
    protected IPlayer player;
    public JdaLink link;
    private boolean repeating;
    private boolean queueRepeating;

    public Player() {
        this.trackQueue = new LinkedList<>();
        this.repeating = false;
        this.queueRepeating = false;
    }

    protected void instanciatePlayer(JdaLink link) {
        this.link = link;
        this.player = link.getPlayer();
        player.addListener(this);
    }

    public void play(AudioTrack track) {
        if (track == null) {
            disconnect();
            return;
        }
        if (player.isPaused())
            resume();
        player.playTrack(track);
    }

    public void stop() {
        player.stopTrack();
    }

    public void pause() {
        player.setPaused(true);
    }

    public void resume() {
        player.setPaused(false);
    }

    public void seekTo(long time) {
        player.seekTo(time);
    }

    public void shuffle() {
        Collections.shuffle((List<?>) trackQueue);
    }

    public void setVolume(int volume) {
        player.setVolume(volume);
    }

    public boolean isPlaying() {
        return player.getPlayingTrack() != null;
    }

    public AudioTrack pollTrack() {
        if (trackQueue.isEmpty())
            return null;
        AudioTrack track = trackQueue.poll();
        save();
        return track;
    }
    public void queueTrack(AudioTrack track) {
        trackQueue.add(track);
        if (!isPlaying())
            play(pollTrack());
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        handleTrackEnd(player, track, endReason);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        handleTrackEnd(player, track, AudioTrackEndReason.LOAD_FAILED);
    }

    private void handleTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        switch (reason) {
            case FINISHED:
                AudioTrack nextTrack = pollTrack();
                if (nextTrack == null)
                    disconnect();
                if (queueRepeating)
                    trackQueue.add(track);
                if (repeating) {
                    play(track);
                    return;
                }
                play(nextTrack);
                break;
            case STOPPED:
            case LOAD_FAILED:
                pollTrack();
                play(pollTrack());
                break;
        }
    }

    public abstract void disconnect();
    protected abstract void save();
}
