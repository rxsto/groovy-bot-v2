package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.util.EmbedUtil;
import io.groovybot.bot.util.SafeMessage;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class MusicPlayer extends Player {

    private final Guild guild;
    private final TextChannel channel;
    @Getter
    private final AudioPlayerManager audioPlayerManager;

    protected MusicPlayer(Guild guild, TextChannel channel) {
        super();
        LavalinkManager lavalinkManager = GroovyBot.getInstance().getLavalinkManager();
        this.guild = guild;
        this.channel = channel;
        instanciatePlayer(lavalinkManager.getLavalink().getLink(guild));
        getPlayer().addListener(getScheduler());
        audioPlayerManager = lavalinkManager.getAudioPlayerManager();
    }

    public void connect(VoiceChannel channel) {
        link.connect(channel);
    }

    public boolean checkConnect(CommandEvent event) {
        if (!event.getGuild().getSelfMember().hasPermission(event.getMember().getVoiceState().getChannel(), Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.nopermission.title"), event.translate("phrases.join.nopermission.description")));
            return false;
        }
        return true;
    }

    public void leave() {
        trackQueue.clear();
        link.disconnect();
    }

    @Override
    public void onEnd(boolean announce) {
        if (announce)
            SafeMessage.sendMessage(channel, EmbedUtil.success("The queue ended!", "Why not queue more songs?"));
        link.disconnect();
        stop();
    }

    @Override
    protected void save() {
        GroovyBot.getInstance().getMusicPlayerManager().update(guild, this);
    }

    @Override
    public void announceSong(AudioPlayer audioPlayer, AudioTrack track) {
        channel.sendMessage(EmbedUtil.play("Now Playing", String.format("%s (%s)", track.getInfo().title, track.getInfo().author)).build()).queue();
    }


    @Override
    public IPlayer getPlayer() {
        this.player = this.player == null ? new LavaplayerPlayerWrapper(getAudioPlayerManager().createPlayer()) : this.player;
        return this.player;
    }

    public void queueSongs(CommandEvent event, boolean force) {
        String keyword = event.getArguments();
        boolean isUrl = true;

        if (!keyword.startsWith("http://") && !keyword.startsWith("https://")) {
            keyword = "ytsearch: " + keyword;
            isUrl = false;
        }

        Message infoMessage = SafeMessage.sendMessageBlocking(event.getChannel(), EmbedUtil.info(event.translate("phrases.searching.title"), String.format(event.translate("phrases.searching.description"), event.getArguments())));


        final boolean isURL = isUrl;
        System.out.println(keyword);
        getAudioPlayerManager().loadItem(keyword, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                queueTrack(audioTrack, force);
                infoMessage.editMessage(EmbedUtil.success(event.translate("phrases.searching.trackloaded.title"), String.format(event.translate("phrases.searching.trackloaded.description"), audioTrack.getInfo().title)).build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> tracks = audioPlaylist.getTracks();
                tracks = tracks.stream().limit(50 - getQueueSize()).collect(Collectors.toList());

                if (isURL) {
                    queueTracks(tracks.toArray(new AudioTrack[0]));
                    infoMessage.editMessage(EmbedUtil.success(event.translate("phrases.searching.playlistloaded.title"), String.format(event.translate("phrases.searching.playlistloaded.description"), audioPlaylist.getName())).build()).queue();
                    return;
                }

                final AudioTrack track = tracks.get(0);
                queueTrack(track, force);
                infoMessage.editMessage(EmbedUtil.success(event.translate("phrases.searching.trackloaded.title"), String.format(event.translate("phrases.searching.trackloaded.description"), track.getInfo().title)).build()).queue();
            }

            @Override
            public void noMatches() {
                infoMessage.editMessage(EmbedUtil.error(event.translate("phrases.searching.nomatches.title"), event.translate("phrases.searching.nomatches.description")).build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                infoMessage.editMessage(EmbedUtil.error(event).build()).queue();
                log.error("[PlayCommand] Error while loading track!", e);
            }
        });
    }
}
