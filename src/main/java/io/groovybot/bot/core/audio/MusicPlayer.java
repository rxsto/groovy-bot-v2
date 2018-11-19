package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.permission.UserPermissions;
import io.groovybot.bot.core.entity.EntityProvider;
import io.groovybot.bot.util.*;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
public class MusicPlayer extends Player implements Runnable {

    @Getter
    private final Guild guild;
    @Getter
    private final AudioPlayerManager audioPlayerManager;
    private final ScheduledExecutorService scheduler;
    @Getter
    @Setter
    private TextChannel channel;
    @Getter
    @Setter
    private AudioTrack previousTrack;
    private boolean inProgress;

    protected MusicPlayer(Guild guild, TextChannel channel, YoutubeUtil youtubeClient) {
        super(youtubeClient);
        LavalinkManager lavalinkManager = GroovyBot.getInstance().getLavalinkManager();
        this.guild = guild;
        this.channel = channel;
        this.previousTrack = null;
        this.inProgress = false;
        instanciatePlayer(LavalinkManager.getLavalink().getLink(guild));
        getPlayer().addListener(getScheduler());
        audioPlayerManager = lavalinkManager.getAudioPlayerManager();
        scheduler = Executors.newSingleThreadScheduledExecutor(new NameThreadFactory("LeaveListener"));
        scheduler.scheduleAtFixedRate(this, 0, 5, TimeUnit.MINUTES);
    }

    public void connect(VoiceChannel channel) {
        link.connect(channel); // TODO: CHECK IF FIX POSSIBLE!
        Objects.requireNonNull(link.getGuild()).getAudioManager().setSelfDeafened(true);
    }

    public boolean checkConnect(CommandEvent event) {
        if (event.getMember().getVoiceState().getChannel() == null) return false;
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
        if (inProgress) return;
        trackQueue.clear();
        if (!this.getGuild().getId().equals("403882830225997825") || GroovyBot.getInstance().getGuildCache().get(guild.getIdLong()).isAutoLeave())
            if (link.getPlayer() != null) LavalinkManager.getLavalink().getLink(guild.getId()).disconnect();
    }

    public void leave(String cause) {
        if (inProgress) return;
        if (!GroovyBot.getInstance().getGuildCache().get(guild.getIdLong()).isAutoLeave()) return;
        if (channel != null) SafeMessage.sendMessage(channel, EmbedUtil.noTitle(cause));
        leave();
    }

    @Override
    public void onEnd(boolean announce) {
        if (announce)
            SafeMessage.sendMessage(channel, EmbedUtil.success("The queue ended!", "Why not **queue** more songs?"));
        stop();
        leave();
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

    public void queueSongs(CommandEvent event) {
        UserPermissions userPermissions = EntityProvider.getUser(event.getAuthor().getIdLong()).getPermissions();
        Permissions tierTwo = Permissions.tierTwo();

        if (trackQueue.size() >= 25 && !tierTwo.isCovered(userPermissions, event)) {
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.fullqueue.title"), event.translate("phrases.fullqueue.description")));
            return;
        }

        String keyword = event.getArguments();

        boolean isUrl = true;

        final boolean isSoundcloud;
        final boolean isForce;
        final boolean isTop;

        if (keyword.contains("-soundcloud") || keyword.contains("-sc")) {
            isSoundcloud = true;
            keyword = keyword.replaceAll("-soundcloud", "").replaceAll("-sc", "");
        } else isSoundcloud = false;

        if (keyword.contains("-forceplay") || keyword.contains("-fp") || keyword.contains("-skip") || keyword.contains("-force")) {
            isForce = true;
            keyword = keyword.replaceAll("-forceplay", "").replaceAll("-fp", "").replaceAll("-skip", "").replaceAll("-force", "");
        } else isForce = false;

        if (keyword.contains("-playtop") || keyword.contains("-pt") || keyword.contains("-top")) {
            isTop = true;
            keyword = keyword.replaceAll("-playtop", "").replaceAll("-pt", "").replaceAll("-top", "");
        } else isTop = false;

        Message infoMessage = SafeMessage.sendMessageBlocking(event.getChannel(), EmbedUtil.info(event.translate("phrases.searching.title"), String.format(event.translate("phrases.searching.description"), keyword)));

        if (!keyword.startsWith("http://") && !keyword.startsWith("https://")) {
            if (isSoundcloud) keyword = "scsearch: " + keyword;
            else keyword = "ytsearch: " + keyword;
            isUrl = false;
        }

        final boolean isURL = isUrl;

        inProgress = true;

        getAudioPlayerManager().loadItem(keyword, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                if (!checkSong(audioTrack)) return;
                queueTrack(audioTrack, isForce, isTop);
                queuedTrack(audioTrack, infoMessage, event);
                inProgress = false;
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> tracks = audioPlaylist.getTracks();

                if (tracks.isEmpty()) {
                    SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event));
                    return;
                }

                if (!tierTwo.isCovered(userPermissions, event))
                    tracks = tracks.stream()
                            .limit(50 - getQueueSize())
                            .filter(track -> track.getDuration() < 3600000)
                            .collect(Collectors.toList());

                if (tracks.isEmpty()) {
                    SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.fullqueue.title"), event.translate("phrases.fullqueue.description")));
                    return;
                }

                if (isURL) {
                    queueTracks(tracks.toArray(new AudioTrack[0]));
                    inProgress = false;
                    if (!tierTwo.isCovered(userPermissions, event))
                        SafeMessage.editMessage(infoMessage, EmbedUtil.success(event.translate("phrases.searching.playlistloaded.nopremium.title"), String.format(event.translate("phrases.searching.playlistloaded.nopremium.description"), tracks.size(), audioPlaylist.getName())));
                    else
                        SafeMessage.editMessage(infoMessage, EmbedUtil.success(event.translate("phrases.searching.playlistloaded.title"), String.format(event.translate("phrases.searching.playlistloaded.description"), tracks.size(), audioPlaylist.getName())));
                    return;
                }

                final AudioTrack track = tracks.get(0);

                if (!checkSong(track)) return;

                queueTrack(track, isForce, isTop);
                queuedTrack(track, infoMessage, event);
                inProgress = false;
            }

            @Override
            public void noMatches() {
                SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.searching.nomatches.title"), event.translate("phrases.searching.nomatches.description")));
                inProgress = false;
            }

            @Override
            public void loadFailed(FriendlyException e) {
                handleFailedLoads(e, infoMessage, event);
                inProgress = false;
            }

            private boolean checkSong(AudioTrack track) {
                return !MusicPlayer.this.checkSong(track, userPermissions, event, infoMessage);
            }
        });
    }

    private boolean checkSong(AudioTrack track, UserPermissions userPermissions, CommandEvent event, Message infoMessage) {
        if (track.getDuration() > 3600000 && !Permissions.tierTwo().isCovered(userPermissions, event)) {
            SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.patreon.songduration.title"), event.translate("phrases.patreon.songduration.description")));
            if (trackQueue.isEmpty()) leave();
            return true;
        }
        return false;
    }

    private void handleFailedLoads(FriendlyException e, Message infoMessage, CommandEvent event) {
        SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.searching.error.title"), e.getCause() != null ? String.format("**%s**\n%s", e.getMessage(), e.getCause().getMessage()) : String.format("**%s**", e.getMessage())));
        if (e.getCause() != null) log.error("[MusicPlayer] Error while loading track!", e);
    }

    private void queuedTrack(AudioTrack track, Message infoMessage, CommandEvent event) {
        if (track.getInfo().isStream)
            SafeMessage.editMessage(infoMessage, EmbedUtil.success(event.translate("phrases.searching.streamloaded.title"), String.format(event.translate("phrases.searching.streamloaded.description"), track.getInfo().title)));
        else
            SafeMessage.editMessage(infoMessage, EmbedUtil.success(event.translate("phrases.searching.trackloaded.title"), String.format(event.translate("phrases.searching.trackloaded.description"), track.getInfo().title)));
    }

    public void update() throws SQLException, IOException {
        if (channel != null)
            channel.sendMessage(":warning: Update initialized! Groovy should be back soon!").queue();

        try (Connection connection = GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection()) {
            // Initialize preparedstatement
            PreparedStatement ps = connection.prepareStatement("INSERT INTO queues (guild_id, current_track, current_position, queue, channel_id, text_channel_id, volume) VALUES (?,?,?,?,?,?,?)");

            // Checking if able to update
            if (player.getPlayingTrack() == null || guild.getSelfMember().getVoiceState().getChannel() == null)
                return;

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
            getScheduler().setLoopqueue(false);
            getScheduler().setLoop(false);
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
                    queueTrack(playlist.getTracks().get(0), true, false);
                }

                @Override
                public void noMatches() {
                    if (channel != null)
                        channel.sendMessage(":x: An error occurred! Please contact the developers!").queue();
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    if (channel != null)
                        channel.sendMessage(":x: An error occurred! Please contact the developers!").queue();
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

    @Override
    public void run() {
        if (guild.getSelfMember().getVoiceState().getChannel() != null)
            if (!isPlaying())
                leave("I've **left** the voice-channel because I've been **inactive** for **too long**! If you **would like** to **disable** this you should consider **[donating](https://patreon.com/rxsto)**!");
    }
}
