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

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.commands.music.SearchCommand;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.permission.UserPermissions;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.util.*;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.LavalinkUtil;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static co.groovybot.bot.util.EmbedUtil.info;
import static co.groovybot.bot.util.SafeMessage.sendMessage;

@Log4j2
public class MusicPlayer extends Player implements Runnable {

    @Getter
    private Guild guild;
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
    @Getter
    @Setter
    private String bassboost = "off";
    @Getter
    private int skipVotes;
    @Getter
    private VoiceChannel voiceChannel;

    protected MusicPlayer(Guild guild, TextChannel channel, YoutubeUtil youtubeClient) {
        super(youtubeClient);
        LavalinkManager lavalinkManager = GroovyBot.getInstance().getLavalinkManager();
        this.guild = guild;
        this.channel = channel;
        this.previousTrack = null;
        this.inProgress = false;
        this.voiceChannel = guild.getSelfMember().getVoiceState().getChannel();
        instanciatePlayer(LavalinkManager.getLavalink().getLink(guild));
        getPlayer().addListener(getScheduler());
        audioPlayerManager = lavalinkManager.getAudioPlayerManager();
        scheduler = Executors.newSingleThreadScheduledExecutor(new NameThreadFactory("LeaveListener"));
        scheduler.scheduleAtFixedRate(this, 0, 10, TimeUnit.MINUTES);
        guild.getJDA().addEventListener(this);
    }

    public void connect(VoiceChannel channel) {
        link.connect(channel);
        Objects.requireNonNull(link.getGuild()).getAudioManager().setSelfDeafened(true);
    }

    public boolean checkConnect(CommandEvent event) {
        if (event.getMember().getVoiceState().getChannel() == null) return false;
        if (!event.getGuild().getSelfMember().hasPermission(event.getMember().getVoiceState().getChannel(), Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.nopermission.title"), event.translate("phrases.join.nopermission.description")));
            return false;
        }
        if (event.getMember().getVoiceState().getChannel().getUserLimit() != 0 && !event.getGuild().getSelfMember().hasPermission(event.getMember().getVoiceState().getChannel(), Permission.ADMINISTRATOR) && !event.getGuild().getSelfMember().hasPermission(event.getMember().getVoiceState().getChannel(), Permission.VOICE_MOVE_OTHERS) && event.getMember().getVoiceState().getChannel().getMembers().size() >= event.getMember().getVoiceState().getChannel().getUserLimit()) {
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.channelfull.title"), event.translate("phrases.channelfull.description")));
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
        clearQueue();
        stop();
        LavalinkManager.getLavalink().getLink(guild.getId()).disconnect();
    }

    public void leave(String cause) {
        if (channel != null) SafeMessage.sendMessage(channel, EmbedUtil.small(cause));
        leave();
    }

    @Override
    public void onEnd(boolean announce) {
        //if (inProgress) return;
        if (announce)
            SafeMessage.sendMessage(channel, EmbedUtil.info("Queue Ended", "The queue ended, why don't you add more songs?"));
        if (!GroovyBot.getInstance().getGuildCache().get(guild.getIdLong()).isAutoLeave()) return;
        leave();
    }

    @Override
    public Message announceAutoplay() {
        return SafeMessage.sendMessageBlocking(channel, info("Searching Video", "Searching new AutoPlay video ..."));
    }

    @Override
    public void announceRequeue(AudioTrack track) {
        SafeMessage.sendMessage(channel, info("An error occurred while queueing song!", "An unexpected error occurred while queueing song, trying to requeue now."));
    }

    @Override
    protected void save() {
        GroovyBot.getInstance().getMusicPlayerManager().update(guild, this);
    }

    @Override
    public void announceSong(AudioPlayer audioPlayer, AudioTrack track) {
        if (EntityProvider.getGuild(guild.getIdLong()).isAnnounceSongs())
            SafeMessage.sendMessage(channel, EmbedUtil.play("Now Playing", String.format("Groovy successfully started playing `%s`.", track.getInfo().title), track.getDuration()));
    }

    @Override
    public IPlayer getPlayer() {
        this.player = this.player == null ? new LavaplayerPlayerWrapper(getAudioPlayerManager().createPlayer()) : this.player;
        return this.player;
    }

    public int removeDups() {
        List<String> fineTracks = new ArrayList<>();
        List<AudioTrack> dups = new ArrayList<>();
        trackQueue.forEach(t -> {
            if (fineTracks.contains(t.getInfo().title))
                dups.add(t);
            else
                fineTracks.add(t.getInfo().title);
        });
        dups.forEach(t -> trackQueue.remove(t));
        return dups.size();
    }

    public void queueSongs(final CommandEvent event) {
        guild = event.getGuild();

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

        Message infoMessage = SafeMessage.sendMessageBlocking(event.getChannel(), info(event.translate("phrases.searching.title"), String.format(event.translate("phrases.searching.description"), keyword)));

        if (!keyword.startsWith("http://") && !keyword.startsWith("https://")) {
            if (isSoundcloud) keyword = "scsearch: " + keyword;
            else keyword = "ytsearch: " + keyword;
            isUrl = false;
        }

        final boolean isURL = isUrl;

        inProgress = true;

        if (isUrl && keyword.matches("(https?://)?(.*)?spotify\\.com.*"))
            keyword = removeQueryFromUrl(keyword);

        getAudioPlayerManager().loadItem(keyword, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                queueWithChecks(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> tracks = audioPlaylist.getTracks();

                if (tracks.isEmpty()) {
                    SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event));
                    inProgress = false;
                    return;
                }

                if (!tierTwo.isCovered(userPermissions, event))
                    tracks = tracks.stream()
                            .limit(50 - getQueueSize())
                            .filter(track -> track.getDuration() < 3600000)
                            .collect(Collectors.toList());

                if (tracks.isEmpty()) {
                    SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.fullqueue.title"), event.translate("phrases.fullqueue.description")));
                    inProgress = false;
                    return;
                }

                List<AudioTrack> dups = new ArrayList<>();
                if (isURL) {
                    if (EntityProvider.getGuild(guild.getIdLong()).isPreventDups()) {
                        List<AudioTrack> playTracks = new ArrayList<>();
                        tracks.forEach(t -> {
                            if (!checkDups(t))
                                playTracks.add(t);
                            else
                                dups.add(t);
                        });
                        tracks.clear();
                        tracks.addAll(playTracks);
                    }
                    queueTracks(tracks.toArray(new AudioTrack[0]));
                    inProgress = false;

                    if (!tierTwo.isCovered(userPermissions, event))
                        SafeMessage.editMessage(infoMessage, EmbedUtil.success(event.translate("phrases.searching.playlistloaded.nopremium.title"), String.format(event.translate("phrases.searching.playlistloaded.nopremium.description"), tracks.size(), audioPlaylist.getName())));
                    else {
                        SafeMessage.editMessage(infoMessage, EmbedUtil.success(event.translate("phrases.searching.playlistloaded.title"), String.format(event.translate("phrases.searching.playlistloaded.description"), tracks.size(), audioPlaylist.getName())));
                        if (!dups.isEmpty())
                            SafeMessage.sendMessage(event.getChannel(), info(String.format(event.translate("phrases.load.playlist.dups.title"), dups.size()), String.format(event.translate("phrases.load.playlist.dups.description"), EntityProvider.getGuild(guild.getIdLong()).getPrefix())));
                    }
                    inProgress = false;
                    return;
                }
                tracks = tracks.stream().limit(5).collect(Collectors.toList());
                Message infoMessage = SafeMessage.sendMessageBlocking(event.getChannel(), info(event.translate("command.search.results.title"), SearchCommand.buildTrackDescription(tracks)).setFooter(event.translate("command.search.results.footer"), null));
                for (int i = 0; i < tracks.size(); i++) {
                    infoMessage.addReaction(SearchCommand.EMOTES[i]).complete();
                }
                try {
                    new SearchCommand.MusicResult(infoMessage, event.getChannel(), event.getMember(), tracks, GroovyBot.getInstance().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel()));
                } catch (InsufficientPermissionException e) {
                    sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.nopermission.title"), event.translate("phrases.nopermission.manage")));
                }

            }

            private void queueWithChecks(AudioTrack track) {
                if (!checkSong(track)) return;
                if (checkDups(track)) {
                    SafeMessage.editMessage(infoMessage, info(event.translate("phrases.load.single.dups.title"), String.format(event.translate("phrases.load.single.dups.description"), EntityProvider.getGuild(guild.getIdLong()).getPrefix())));
                    inProgress = false;
                    return;
                }
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
    }

    private void queuedTrack(AudioTrack track, Message infoMessage, CommandEvent event) {
        if (track.getInfo().isStream)
            SafeMessage.editMessage(infoMessage, EmbedUtil.success(event.translate("phrases.searching.streamloaded.title"), String.format(event.translate("phrases.searching.streamloaded.description"), track.getInfo().title)));
        else
            SafeMessage.editMessage(infoMessage, EmbedUtil.success(event.translate("phrases.searching.trackloaded.title"), String.format(event.translate("phrases.searching.trackloaded.description"), track.getInfo().title)).setFooter(String.format("Estimated: %s", getQueueLengthMillis() == 0 ? "Now!" : FormatUtil.formatDuration(getQueueLengthMillis())), null));
    }

    public void update() throws SQLException, IOException {
        if (channel != null)
            if (channel.canTalk())
                SafeMessage.sendMessageBlocking(channel, EmbedUtil.small("Update initialized! Groovy should be back soon!"));

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
            stop();

            if (isPaused())
                resume();

            getAudioPlayerManager().loadItem("https://cdn.groovybot.co/sounds/update.mp3", new AudioLoadResultHandler() {
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

    public String removeQueryFromUrl(String url) {
        try {
            return new URIBuilder(url).removeQuery().toString();
        } catch (URISyntaxException e) {
            return url;
        }
    }

    private String getBuildedQueue() throws IOException {
        JSONArray jsonArray = new JSONArray();
        for (AudioTrack audioTrack : trackQueue) {
            jsonArray.put(LavalinkUtil.toMessage(audioTrack));
        }
        return jsonArray.toString();
    }

    private boolean checkDups(AudioTrack audioTrack) {
        if (!EntityProvider.getGuild(guild.getIdLong()).isPreventDups())
            return false;
        return player.getPlayingTrack() != null && player.getPlayingTrack().getInfo().title.equals(audioTrack.getInfo().title) || trackQueue.stream().anyMatch(t -> t.getInfo().title.equals(audioTrack.getInfo().title));
    }

    @Override
    public void run() {
        if (inProgress) return;
        if (!GroovyBot.getInstance().getGuildCache().get(guild.getIdLong()).isAutoLeave()) return;
        if (guild.getSelfMember().getVoiceState().getChannel() == null) return;
        if (!isPlaying())
            leave("I've **left** the voice-channel because I've been **inactive** for **too long**! If you **would like** to **disable** this you should consider **[donating](https://donate.groovybot.co)**!");
        else if (guild.getSelfMember().getVoiceState().getChannel().getMembers().size() == 1)
            leave("I've **left** the voice-channel because I've been **alone** for **too long**! If you **would like** to **disable** this you should consider **[donating](https://donate.groovybot.co)**!");
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    private void handleDisconnect(GuildVoiceLeaveEvent event) {
        if (event.getMember().equals(event.getGuild().getSelfMember())) {
            skipVotes = 0;
            voiceChannel = null;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    private void handleConnect(GuildVoiceJoinEvent event) {
        if (event.getMember().equals(event.getGuild().getSelfMember()))
            voiceChannel = event.getChannelJoined();
    }

    public VoteSkipReason voteSkipAvailable() {
        if (voiceChannel == null)
            return VoteSkipReason.ERROR;
        if (voiceChannel.getMembers().size() == 2)
            return VoteSkipReason.ALONE;
        if (EntityProvider.getGuild(guild.getIdLong()).isDjMode() && voiceChannel.getMembers().stream().noneMatch(member -> new UserPermissions(EntityProvider.getUser(member.getUser().getIdLong()), GroovyBot.getInstance()).isDj(guild)))
            return VoteSkipReason.ALLOWED;
        return VoteSkipReason.DJ_IN_CHANNEL;
    }

    public int getNeededSkipVotes() {
        if (voiceChannel != null)
            return voiceChannel.getMembers().size() / 2;
        return 0;
    }

    public boolean incrementSkipVotes() {
        int needed = getNeededSkipVotes();
        skipVotes++;
        return skipVotes >= needed;
    }

    @Override
    public void resetSkipVotes() {
        skipVotes = 0;
    }

    @Getter
    @RequiredArgsConstructor
    public enum VoteSkipReason {
        ALLOWED(null, null),
        ALONE("phrases.skipped", "command.skip"),
        DJ_IN_CHANNEL("phrases.nopermission.title", "command.voteskip.dj"),
        ERROR("phrases.error", "phrases.internal.error");

        private final String titleTranslationKey;
        private final String descriptionTranslationKey;
    }
}
