package co.groovybot.bot.core.audio.executors.leave;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.audio.executors.PlayerRunnable;

import java.util.concurrent.TimeUnit;

public class IsPausedRunnable extends PlayerRunnable {

    public IsPausedRunnable(MusicPlayer musicPlayer, long delay, long period, TimeUnit timeUnit) {
        super(musicPlayer, delay, period, timeUnit);
    }

    @Override
    public void execute() {
        if (getMusicPlayer().isPaused())
            getMusicPlayer().leave(getMusicPlayer().translate("phrases.left.paused"));
    }
}
