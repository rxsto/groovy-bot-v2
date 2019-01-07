package co.groovybot.bot.core.audio;

import com.google.common.util.concurrent.Futures;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class PlayerCheckHandler {

    private final MusicPlayer player;
    private final ScheduledExecutorService scheduler;

    private Future isAloneFuture = Futures.immediateFuture(null);
    private Future isNotPlayingFuture = Futures.immediateFuture(null);
    private Future isPausedFuture = Futures.immediateFuture(null);

    public PlayerCheckHandler(MusicPlayer player, int poolSize) {
        this.player = player;
        this.scheduler = Executors.newScheduledThreadPool(poolSize);
    }

    private void isAlone() {
        if (player.checkLeave())
            if (player.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().size() == 1)
                player.leave(player.translate("phrases.left.alone"));
    }

    private void isNotPlaying() {
        if (player.checkLeave())
            if (!player.isPlaying())
                player.leave(player.translate("phrases.left.notplaying"));
    }

    private void isPaused() {
        if (player.checkLeave())
            if (player.isPaused())
                player.leave(player.translate("phrases.left.paused"));
    }

    @SubscribeEvent
    private void onJoin(GuildVoiceJoinEvent event) {
        handleVoiceEvent(event, voiceState -> {
            if (voiceState.getChannel().getMembers().size() > 1)
                cancelFuture(isAloneFuture);
        });
    }

    @SubscribeEvent
    private void onLeave(GuildVoiceLeaveEvent event) {
        handleVoiceEvent(event, voiceState -> {
            if (voiceState.getChannel().getMembers().size() == 1)
                isAloneFuture = scheduler.scheduleAtFixedRate(this::isAlone, 20, 20, TimeUnit.SECONDS);
        });
    }

    @SubscribeEvent
    private void onMove(GuildVoiceMoveEvent event) {
        handleVoiceEvent(event, voiceState -> {
            if (voiceState.getChannel().getMembers().size() == 1)
                isAloneFuture = scheduler.scheduleAtFixedRate(this::isAlone, 20, 20, TimeUnit.SECONDS);
        });
    }

    private void handleVoiceEvent(GenericGuildVoiceEvent event, Consumer<GuildVoiceState> handler) {
        handleVoiceEvent(event, handler, true);
    }

    private void handleVoiceEvent(GenericGuildVoiceEvent event, Consumer<GuildVoiceState> handler, boolean inChannel) {
        GuildVoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();
        if (voiceState.inVoiceChannel() == inChannel)
            handler.accept(voiceState);
    }

    private void cancelFuture(Future future) {
        if (!future.isDone() && !future.isCancelled())
            future.cancel(false);
    }

    public void handlePlayerLeave() {
        cancelFuture(isNotPlayingFuture);
    }

    public void handlePlayerJoin() {
        isNotPlayingFuture = scheduler.scheduleAtFixedRate(this::isNotPlaying, 10, 10, TimeUnit.MINUTES);
    }

    public void handleTrackPause() {
        isPausedFuture = scheduler.scheduleAtFixedRate(this::isPaused, 30, 30, TimeUnit.MINUTES);
    }

    public void handleTrackResume() {
        cancelFuture(isPausedFuture);
    }
}
