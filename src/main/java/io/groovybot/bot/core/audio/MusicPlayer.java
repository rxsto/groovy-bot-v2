package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.permission.UserPermissions;
import io.groovybot.bot.core.entity.EntityProvider;
import io.groovybot.bot.util.EmbedUtil;
import io.groovybot.bot.util.FormatUtil;
import io.groovybot.bot.util.SafeMessage;
import io.groovybot.bot.util.YoutubeUtil;
import lavalink.client.LavalinkUtil;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import org.json.JSONArray;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
public class MusicPlayer extends Player {

    @Getter
    private final Guild guild;
    @Getter
    private final AudioPlayerManager audioPlayerManager;
    @Getter
    @Setter
    private TextChannel channel;
    @Getter
    @Setter
    private AudioTrack previousTrack;

    protected MusicPlayer(Guild guild, TextChannel channel, YoutubeUtil youtubeClient) {
        super(youtubeClient);
        LavalinkManager lavalinkManager = GroovyBot.getInstance().getLavalinkManager();
        this.guild = guild;
        this.channel = channel;
        this.previousTrack = null;
        instanciatePlayer(LavalinkManager.getLavalink().getLink(guild));
        getPlayer().addListener(getScheduler());
        audioPlayerManager = lavalinkManager.getAudioPlayerManager();
    }

    public void connect(VoiceChannel channel) {
        link.connect(channel);
        Objects.requireNonNull(link.getGuild()).getAudioManager().setSelfDeafened(true);
    }

    public boolean checkConnect(CommandEvent event) {
        if (!event.getGuild().getSelfMember().hasPermission(event.getMember().getVoiceState().getChannel(), Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.nopermission.title"), event.translate("phrases.join.nopermission.description")));
            return false;
        }
        final GuildVoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();
        if (voiceState.inVoiceChannel() && voiceState.getChannel().getMembers().size() != 1 && !Permissions.djMode().isCovered(event.getPermissions(), event)) {
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.djrequired.title"), event.translate("phrases.djrequired.description")));
            return false;
        }
        return true;
    }

    public void leave() {
        trackQueue.clear();
        if (!this.getGuild().getId().equals("403882830225997825"))
            link.disconnect();
    }

    @Override
    public void onEnd(boolean announce) {
        if (announce)
            SafeMessage.sendMessage(channel, EmbedUtil.success("The queue ended!", "Why not **queue** more songs?"));
        if (!this.getGuild().getId().equals("403882830225997825"))
            if (link != null)
                link.disconnect();
        stop();
    }

    @Override
    public Message announceAutoplay() {
        return SafeMessage.sendMessageBlocking(channel, EmbedUtil.info("Searching video!", "Searching new autoplay video ..."));
    }

    @Override
    public void announceRequeue(AudioTrack track) {
        SafeMessage.sendMessage(channel, EmbedUtil.success("An error occurred while queueing song!", "An unexpected error occurred while queueing song, trying to requeue now."));
    }

    @Override
    protected void save() {
        GroovyBot.getInstance().getMusicPlayerManager().update(guild, this);
    }

    @Override
    public void announceSong(AudioPlayer audioPlayer, AudioTrack track) {
        if (EntityProvider.getGuild(guild.getIdLong()).isAnnounceSongs())
            SafeMessage.sendMessage(channel, EmbedUtil.play("Now Playing", FormatUtil.formatTrack(track)));
    }


    @Override
    public IPlayer getPlayer() {
        this.player = this.player == null ? new LavaplayerPlayerWrapper(getAudioPlayerManager().createPlayer()) : this.player;
        return this.player;
    }

    public void queueSongs(CommandEvent event, boolean force, boolean playtop) {
        UserPermissions userPermissions = EntityProvider.getUser(event.getAuthor().getIdLong()).getPermissions();
        Permissions tierTwo = Permissions.tierTwo();
        if (trackQueue.size() >= 25 && !tierTwo.isCovered(userPermissions, event)) {
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.fullqueue.title"), event.translate("phrases.fullqueue.description")));
            return;
        }
        String keyword = event.getArguments();
        boolean isUrl = true;

        if (!keyword.startsWith("http://") && !keyword.startsWith("https://")) {
            keyword = "ytsearch: " + keyword;
            isUrl = false;
        }

        if ((keyword.startsWith("http://") || keyword.startsWith("https://")) && keyword.contains("spotify")) {
            Track track = event.getBot().getSpotifyClient().getTrack(keyword);
            if (track != null) {
                SafeMessage.sendMessageBlocking(
                        event.getChannel(),
                        EmbedUtil.info("Spotify Search Query", track.getArtists()[0].getName() + " - " + track.getName())
                );
                keyword = "ytsearch: " + track.getArtists()[0].getName() + " - " + track.getName();
                log.info(keyword);
                isUrl = false;
            }
        }

        Message infoMessage = SafeMessage.sendMessageBlocking(event.getChannel(), EmbedUtil.info(event.translate("phrases.searching.title"), String.format(event.translate("phrases.searching.description"), event.getArguments())));

        final boolean isURL = isUrl;
        getAudioPlayerManager().loadItem(keyword, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                if (!checkSong(audioTrack))
                    return;
                queueTrack(audioTrack, force, playtop, event.getAuthor());
                queuedTrack(audioTrack, infoMessage, event);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> tracks = audioPlaylist.getTracks();
                if (!tierTwo.isCovered(userPermissions, event))
                    tracks = tracks.stream()
                            .limit(25 - getQueueSize())
                            .filter(track -> track.getDuration() < 3600000)
                            .collect(Collectors.toList());

                if (tracks.isEmpty()) {
                    SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event));
                    return;
                }

                if (isURL) {
                    queueTracks(event.getAuthor(), tracks.toArray(new AudioTrack[0]));
                    SafeMessage.editMessage(infoMessage, EmbedUtil.success(event.translate("phrases.searching.playlistloaded.title"), String.format(event.translate("phrases.searching.playlistloaded.description"), audioPlaylist.getName())));
                    return;
                }

                final AudioTrack track = tracks.get(0);

                if (!checkSong(track))
                    return;
                queueTrack(track, force, playtop, event.getAuthor());
                queuedTrack(track, infoMessage, event);
            }

            @Override
            public void noMatches() {
                SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.searching.nomatches.title"), event.translate("phrases.searching.nomatches.description")));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                final String message = e.getMessage().toLowerCase();

                if (message.contains("unknown file format")) {
                    SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.searching.unknownformat.title"), event.translate("phrases.searching.unknownformat.description")));
                    return;
                }

                if (message.contains("the playlist is private")) {
                    SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.searching.private.title"), event.translate("phrases.searching.private.description")));
                    return;
                }

                if (message.contains("this video is not available")) {
                    SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.searching.unavailable.title"), event.translate("phrases.searching.unavailable.description")));
                    return;
                }

                if (message.contains("the uploader has not made this video available in your country")) {
                    SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.searching.country.title"), event.translate("phrases.searching.country.description")));
                    return;
                }

                if (message.contains("this video contains content from umg, who has blocked it in your country on copyright grounds")) {
                    SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.searching.copyright.title"), event.translate("phrases.searching.copyright.description")));
                    return;
                }

                SafeMessage.editMessage(infoMessage, EmbedUtil.error(event));
                log.error("[PlayCommand] Error while loading track!", e);
            }

            private boolean checkSong(AudioTrack track) {
                if (track.getDuration() > 3600000 && !Permissions.tierTwo().isCovered(userPermissions, event)) {
                    SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.patreon.songduration.title"), event.translate("phrases.patreon.songduration.description")));
                    if (trackQueue.isEmpty())
                        leave();
                    return false;
                }
                return true;
            }
        });
    }

    private void queuedTrack(AudioTrack track, Message infoMessage, CommandEvent event) {
        if (track.getInfo().isStream)
            SafeMessage.editMessage(infoMessage, EmbedUtil.success(event.translate("phrases.searching.streamloaded.title"), String.format(event.translate("phrases.searching.streamloaded.description"), track.getInfo().title)));
        else
            SafeMessage.editMessage(infoMessage, EmbedUtil.success(event.translate("phrases.searching.trackloaded.title"), String.format(event.translate("phrases.searching.trackloaded.description"), track.getInfo().title)));
    }

    public void update() throws SQLException, IOException {
        try (Connection connection = GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection()) {
            // Initialize preparedstatement
            PreparedStatement ps = connection.prepareStatement("INSERT INTO queues (guild_id, current_track, current_position, queue, channel_id, text_channel_id, volume) VALUES (?,?,?,?,?,?,?)");

            // Set values for preparedstatement
            ps.setLong(1, guild.getIdLong());
            ps.setString(2, LavalinkUtil.toMessage(player.getPlayingTrack()));
            ps.setLong(3, player.getTrackPosition());
            ps.setString(4, getBuildedQueue());
            ps.setLong(5, guild.getSelfMember().getVoiceState().getChannel().getIdLong());
            ps.setLong(6, channel.getIdLong());
            ps.setInt(7, player.getVolume());


            ps.execute();
            this.clearQueue();
            getScheduler().setShuffle(false);
            getScheduler().setQueueRepeating(false);
            getScheduler().setRepeating(false);
            setVolume(100);
            if (isPaused())
                resume();
            getAudioPlayerManager().loadItem("https://cdn.groovybot.gq/sounds/update.mp3", new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    queueTrack(track, true, false);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {

                }

                @Override
                public void noMatches() {

                }

                @Override
                public void loadFailed(FriendlyException exception) {

                }
            });
        }
    }

    private String getBuildedQueue() throws IOException {
        JSONArray jsonArray = new JSONArray();
        for (AudioTrack audioTrack : trackQueue) {
            jsonArray.put(LavalinkUtil.toMessage(audioTrack));
        }
        return jsonArray.toString();
    }
}
