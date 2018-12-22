/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergeij Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package co.groovybot.bot.core.audio;

import co.groovybot.bot.util.SafeMessage;
import co.groovybot.bot.util.YoutubeUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.IPlayer;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Message;

import java.util.*;

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

    public Player(YoutubeUtil youtubeClient) {
        this.trackQueue = new LinkedList<>();
        this.scheduler = new Scheduler(this);
        this.youtubeClient = youtubeClient;
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
        if (trackQueue.isEmpty()) return null;
        AudioTrack track = trackQueue.poll();
        save();
        return track;
    }

    public void queueTrack(AudioTrack audioTrack, boolean force, boolean playtop) {
        if (force) {
            play(audioTrack, false);
            return;
        }

        if (playtop) ((LinkedList<AudioTrack>) trackQueue).addFirst(audioTrack);
        else trackQueue.add(audioTrack);

        if (!isPlaying()) play(pollTrack(), false);
    }

    public void queueTracks(AudioTrack... tracks) {
        List<AudioTrack> trackList = new ArrayList<>();
        Collections.addAll(trackList, tracks);
        trackQueue.addAll(trackList);
        if (!isPlaying()) play(pollTrack(), false);
    }

    public void skipTo(int delimiter) {
        if (scheduler.isLoopqueue()) trackQueue.add(player.getPlayingTrack());

        if (delimiter == 1) {
            play(pollTrack(), false);
            return;
        }

        for (int i = 1; i < delimiter; i++) pollTrack();
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
        return scheduler.isLoop();
    }

    public boolean queueLoopEnabled() {
        return scheduler.isLoopqueue();
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
