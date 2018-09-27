package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.IPlayer;
import lombok.Getter;

import java.util.*;

public abstract class Player {

    @Getter
    public Queue<AudioTrack> trackQueue;
    @Getter
    protected IPlayer player;
    @Getter
    public JdaLink link;
    @Getter
    private final Scheduler scheduler;

    public Player() {
        this.trackQueue = new LinkedList<>();
        this.scheduler = new Scheduler(this);
    }

    protected void instanciatePlayer(JdaLink link) {
        this.link = link;
        this.player = link.getPlayer();
    }

    protected abstract AudioPlayerManager getAudioPlayerManager();

    public void play(AudioTrack track) {
        if (track == null) {
            onEnd(false);
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

    public void skipTo(int delimiter) {
        if (delimiter == 1) {
            play(pollTrack());
            return;
        }
        for (int i = 1; i < delimiter; i++) {
            pollTrack();
        }
        play(pollTrack());
    }


    protected abstract void save();

    public int getQueueSize() {
        return trackQueue.size();
    }

    public abstract void announceSong(AudioPlayer audioPlayer, AudioTrack track);

    public abstract void onEnd(boolean announce);

    public void clearQueue() {
        trackQueue.clear();
    }

    public boolean isPaused() {
        return player.isPaused();
    }

    public boolean loopEnabled() {
        return scheduler.isRepeating();
    }

    public boolean queueLoopEnabled() {
        return scheduler.isQueueRepeating();
    }

    public void skip() {
        skipTo(1);
    }

    public void purgeQueue() {
        trackQueue.clear();
    }

}
