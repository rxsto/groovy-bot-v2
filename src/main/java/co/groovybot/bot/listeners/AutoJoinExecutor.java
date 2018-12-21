package co.groovybot.bot.listeners;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.entity.Guild;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AutoJoinExecutor {

    private final GroovyBot bot;

    @SubscribeEvent
    private void handleVoiceChannelLeave(GuildVoiceLeaveEvent event) {
        Guild guild = bot.getGuildCache().get(event.getGuild());
        VoiceChannel channel = event.getChannelLeft();
        if (!event.getMember().getUser().isBot() && guild.hasAutoJoinChannel() && channel.getIdLong() == guild.getAutoJoinChannelId() && guild.getAutoJoinChannel().getMembers().contains(event.getGuild().getSelfMember()) && channel.getMembers().size() == 1)
            bot.getMusicPlayerManager().getExistingPlayer(event.getGuild()).leave("No membery anymore in AutoJoin:tm: channel!");
    }

    @SubscribeEvent
    private void handleVoiceMove(GuildVoiceMoveEvent event) {
        Guild guild = bot.getGuildCache().get(event.getGuild());
        long autoChannelId = guild.getAutoJoinChannelId();
        if (event.getChannelJoined().getIdLong() == autoChannelId)
            handleVoiceChannelJoin(new GuildVoiceJoinEvent(event.getJDA(), event.getResponseNumber(), event.getMember()));
        else if (event.getChannelLeft().getIdLong() == autoChannelId)
            handleVoiceChannelLeave(new GuildVoiceLeaveEvent(event.getJDA(), event.getResponseNumber(), event.getMember(), event.getChannelLeft()));
    }

    @SubscribeEvent
    private void handleVoiceChannelJoin(GuildVoiceJoinEvent event) {
        Guild guild = bot.getGuildCache().get(event.getGuild());
        VoiceChannel channel = event.getChannelJoined();
        if (!event.getMember().getUser().isBot() && guild.hasAutoJoinChannel() && channel.getIdLong() == guild.getAutoJoinChannelId() && !guild.getAutoJoinChannel().getMembers().contains(event.getGuild().getSelfMember()))
            bot.getMusicPlayerManager().getPlayer(event.getGuild(), null).connect(channel);
    }

    private <T extends GenericGuildVoiceEvent> void doChecks(T event, BiConsumer<T, Guild> callback) {
        System.out.println(1);
        Guild guild = bot.getGuildCache().get(event.getGuild());
        System.out.println(2);
        VoiceChannel channel = event instanceof GuildVoiceLeaveEvent ? ((GuildVoiceLeaveEvent) event).getChannelLeft() : event.getVoiceState().getChannel();
        System.out.println(channel);
        if (!event.getMember().getUser().isBot() && guild.hasAutoJoinChannel() && channel != null && channel.getIdLong() == guild.getAutoJoinChannelId())
            callback.accept(event, guild);
    }
}
