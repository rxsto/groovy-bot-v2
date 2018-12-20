package co.groovybot.bot.listeners;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.core.entity.Guild;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@Log4j2
public class AutopauseListener {

    @SubscribeEvent
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if (event.getChannelLeft().getMembers().stream().anyMatch(m -> m.getUser().getIdLong() == event.getGuild().getSelfMember().getUser().getIdLong()) && event.getChannelLeft().getMembers().size() == 1)
            handleAutopauseStart(event);
        if (event.getChannelJoined().getMembers().stream().anyMatch(m -> m.getUser().getIdLong() == event.getGuild().getSelfMember().getUser().getIdLong()) && event.getChannelJoined().getMembers().size() > 1)
            handleAutopauseStop(event);
    }

    @SubscribeEvent
    private void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (event.getChannelLeft().getMembers().stream().anyMatch(m -> m.getUser().getIdLong() == event.getGuild().getSelfMember().getUser().getIdLong()) && event.getChannelLeft().getMembers().size() == 1)
            handleAutopauseStart(event);
    }

    @SubscribeEvent
    private void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (event.getChannelJoined().getMembers().stream().anyMatch(m -> m.getUser().getIdLong() == event.getGuild().getSelfMember().getUser().getIdLong()) && event.getChannelJoined().getMembers().size() > 1)
            handleAutopauseStop(event);
    }

    private void handleAutopauseStart(GenericGuildVoiceEvent event) {
        Guild guild = EntityProvider.getGuild(event.getGuild().getIdLong());
        if (!guild.isAutoPause())
            return;
        MusicPlayer musicPlayer = GroovyBot.getInstance().getMusicPlayerManager().getExistingPlayer(event.getGuild());
        if (musicPlayer == null)
            return;
        musicPlayer.getPlayer().setPaused(true);
    }

    private void handleAutopauseStop(GenericGuildVoiceEvent event) {
        Guild guild = EntityProvider.getGuild(event.getGuild().getIdLong());
        if (!guild.isAutoPause())
            return;
        MusicPlayer musicPlayer = GroovyBot.getInstance().getMusicPlayerManager().getExistingPlayer(event.getGuild());
        if (musicPlayer == null)
            return;
        musicPlayer.getPlayer().setPaused(false);
    }

}
