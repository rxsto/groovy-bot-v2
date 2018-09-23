package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.IPlayer;
import lombok.Getter;

import java.util.*;

public abstract class Player {

    public Queue<AudioTrack> trackQueue;
    @Getter
    protected IPlayer player;
    public JdaLink link;

    public Player() {
        this.trackQueue = new LinkedList<>();
    }

    protected void instanciatePlayer(JdaLink link) {
        this.link = link;
        this.player = link.getPlayer();
    }

    protected abstract AudioPlayerManager getAudioPlayerManager();

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

    public void queueTrack(AudioTrack track, boolean force) {
        if (force)
            ((LinkedList<AudioTrack>) trackQueue).addFirst(track);
        trackQueue.add(track);
        if (!isPlaying())
            play(pollTrack());
    }

    public void queueTracks(AudioTrack... tracks) {
        trackQueue.addAll(Arrays.asList(tracks));
        if (!isPlaying())
            play(pollTrack());
    }


    public abstract void disconnect();

    protected abstract void save();

    public int getQueueSize() {
        return trackQueue.size();
    }

    public abstract void announceSong(AudioPlayer audioPlayer, AudioTrack track);
}
