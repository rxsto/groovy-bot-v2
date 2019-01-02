package co.groovybot.bot.core.audio.executors.leave;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.audio.executors.PlayerRunnable;

import java.util.concurrent.TimeUnit;

public class IsAloneRunnable extends PlayerRunnable {

    public IsAloneRunnable(MusicPlayer musicPlayer, long delay, long period, TimeUnit timeUnit) {
        super(musicPlayer, delay, period, timeUnit);
    }

    @Override
    public void execute() {
        if (getMusicPlayer().getGuild().getSelfMember().getVoiceState().getChannel().getMembers().size() == 1)
            getMusicPlayer().leave(getMusicPlayer().translate("phrases.left.alone"));
    }
}
