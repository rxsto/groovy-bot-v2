package co.groovybot.bot.core.audio.executors;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.util.NameThreadFactory;
import lombok.Getter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class PlayerRunnable implements Runnable {

    private MusicPlayer musicPlayer;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledFuture;

    public PlayerRunnable(MusicPlayer musicPlayer, long delay, long period, TimeUnit timeUnit) {
        this.musicPlayer = musicPlayer;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(new NameThreadFactory("PlayerRunnable"));
        this.scheduledFuture = this.scheduler.scheduleAtFixedRate(this, delay, period, timeUnit);
    }

    @Override
    public void run() {
        if (!musicPlayer.checkLeave()) return;
        execute();
    }

    public abstract void execute();
}
