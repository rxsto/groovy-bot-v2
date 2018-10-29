package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.util.YoutubeUtil;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.IPlayer;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Player {

    @Getter
    private final Scheduler scheduler;
    @Getter
    public Queue<AudioTrack> trackQueue;
    @Getter
    public JdaLink link;
    @Getter
    protected IPlayer player;
    protected YoutubeUtil youtubeClient;
    private final SelfUser selfUser;

    public Player(YoutubeUtil youtubeClient) {
        this.trackQueue = new LinkedList<>();
        this.scheduler = new Scheduler(this);
        this.youtubeClient = youtubeClient;
        this.selfUser = GroovyBot.getInstance().getShardManager().getApplicationInfo().getJDA().getSelfUser();
    }

    protected void instanciatePlayer(JdaLink link) {
        this.link = link;
        this.player = link.getPlayer();
    }

    protected abstract AudioPlayerManager getAudioPlayerManager();

    public void play(AudioTrack track, boolean fail) {
        if (fail)
            announceRequeue(track);
        if (track == null) {
            onEnd(false);
            return;
        }
        if (player.isPaused())
            resume();
        player.playTrack(track);
    }

    public abstract void announceRequeue(AudioTrack track);

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

    public void queueTrack(AudioTrack track, boolean force, boolean playtop) {
        queueTrack(track, force, playtop, selfUser);
    }

    public void queueTrack(AudioTrack track, boolean force, boolean playtop, User requester) {
        QueuedTrack queuedTrack = new QueuedTrack(track, requester);
        if (force) {
            play(queuedTrack, false);
            return;
        }

        if (playtop) {
            ((LinkedList<AudioTrack>) trackQueue).addFirst(queuedTrack);
        } else {
            trackQueue.add(queuedTrack);
        }

        if (!isPlaying())
            play(pollTrack(), false);
    }

    public void queueTracks(AudioTrack... tracks) {
        trackQueue.addAll(Arrays.asList(tracks));
        if (!isPlaying())
            play(pollTrack(), false);
    }

    public void skipTo(int delimiter) {
        if (delimiter == 1) {
            play(pollTrack(), false);
            return;
        }
        for (int i = 1; i < delimiter; i++) {
            pollTrack();
        }
        play(pollTrack(), false);
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

    public boolean shuffleEnabled() {
        return scheduler.isShuffle();
    }

    public void skip() {
        seekTo(player.getPlayingTrack().getDuration());
    }

    public void purgeQueue() {
        trackQueue.clear();
    }

    public abstract Message announceAutoplay();

    public void play(AudioTrack track) {
        play(track, false);
    }
}
