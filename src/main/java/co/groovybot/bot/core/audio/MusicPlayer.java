/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
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
import co.groovybot.bot.core.audio.player.util.AnnounceReason;
import co.groovybot.bot.core.audio.sources.spotify.SpotifySourceManager;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.permission.UserPermissions;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.core.premium.Constants;
import co.groovybot.bot.util.EmbedUtil;
import co.groovybot.bot.util.FormatUtil;
import co.groovybot.bot.util.SafeMessage;
import co.groovybot.bot.util.YoutubeUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
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
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static co.groovybot.bot.util.EmbedUtil.*;
import static co.groovybot.bot.util.SafeMessage.sendMessage;

@Log4j2
public class MusicPlayer extends Player {

    public static final Options CLI_OPTIONS = new Options()
            .addOption(
                    Option.builder("f")
                            .longOpt("forceplay")
                            .desc("Instantly plays the song")
                            .build()
            )
            .addOption(
                    Option.builder("t")
                            .longOpt("playtop")
                            .desc("Adds the song to the top of the queue")
                            .build()
            )
            .addOption(
                    Option.builder("sc")
                            .longOpt("soundcloud")
                            .desc("Searches the song on SoundCloud")
                            .build()
            );

    @Getter
    private final AudioPlayerManager audioPlayerManager;
    @Getter
    private Guild guild;
    @Getter
    @Setter
    private TextChannel channel;
    @Getter
    private VoiceChannel voiceChannel;
    @Getter
    private CommandEvent latestEvent;
    @Getter
    @Setter
    private AudioTrack previousTrack;
    @Getter
    @Setter
    private String bassboost = "off";
    @Getter
    @Setter
    private int skipVotes;

    protected MusicPlayer(Guild guild, TextChannel channel, YoutubeUtil youtubeClient) {
        super(youtubeClient);
        LavalinkManager lavalinkManager = GroovyBot.getInstance().getLavalinkManager();

        this.guild = guild;
        this.channel = channel;
        this.previousTrack = null;
        this.voiceChannel = guild.getSelfMember().getVoiceState().getChannel();

        instanciatePlayer(LavalinkManager.getLavalink().getLink(guild));
        getPlayer().addListener(getScheduler());

        this.audioPlayerManager = lavalinkManager.getAudioPlayerManager();
        this.guild.getJDA().addEventListener(this);
    }

    public void connect(VoiceChannel channel) {
        getHandler().handlePlayerJoin();
        link.connect(channel);
        Objects.requireNonNull(link.getGuild()).getAudioManager().setSelfDeafened(true);
    }

    public boolean checkConnect(CommandEvent event) {
        if (event.getMember().getVoiceState().getChannel() == null)
            return false;

        if (!event.getGuild().getSelfMember().hasPermission(event.getMember().getVoiceState().getChannel(), Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.nopermission"), event.translate("phrases.nopermission.join")));
            return false;
        }

        if (event.getMember().getVoiceState().getChannel().getUserLimit() != 0 && !event.getGuild().getSelfMember().hasPermission(event.getMember().getVoiceState().getChannel(), Permission.ADMINISTRATOR) && !event.getGuild().getSelfMember().hasPermission(event.getMember().getVoiceState().getChannel(), Permission.VOICE_MOVE_OTHERS) && event.getMember().getVoiceState().getChannel().getMembers().size() >= event.getMember().getVoiceState().getChannel().getUserLimit()) {
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.nopermission"), event.translate("phrases.nopermission.channelfull")));
            return false;
        }

        if (event.getMember().getVoiceState().inVoiceChannel() && event.getMember().getVoiceState().getChannel().getMembers().size() > 2 && !Permissions.djMode().isCovered(event.getPermissions(), event)) {
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.nopermission"), event.translate("phrases.nopermission.djmode")));
            return false;
        }

        return true;
    }

    boolean checkLeave() {
        if (!GroovyBot.getInstance().getGuildCache().get(getGuild().getIdLong()).isAutoLeave()) return false;
        return GroovyBot.getInstance().getShardManager().getGuildById(getGuild().getIdLong()).getSelfMember().getVoiceState().inVoiceChannel();
    }

    public void leave() {
        clearQueue();
        stop();
        getHandler().handlePlayerLeave();
        if (GroovyBot.getInstance().getShardManager().getGuildById(getGuild().getIdLong()) != null)
            if (GroovyBot.getInstance().getShardManager().getGuildById(getGuild().getIdLong()).getSelfMember().getVoiceState().inVoiceChannel())
                LavalinkManager.getLavalink().getLink(guild.getId()).disconnect();
    }

    public void leave(String cause) {
        if (channel != null) SafeMessage.sendMessage(channel, EmbedUtil.small(cause));
        leave();
    }

    @Override
    public void onEnd(boolean announce) {
        if (announce)
            SafeMessage.sendMessage(channel, EmbedUtil.small(translate("phrases.queueended")));
        if (!GroovyBot.getInstance().getGuildCache().get(guild.getIdLong()).isAutoLeave()) return;
        leave();
    }

    @Override
    public void announce(AudioTrack track, AnnounceReason reason) {
        switch (reason) {
            case SONG:
                if (EntityProvider.getGuild(guild.getIdLong()).isAnnounceSongs())
                    SafeMessage.sendMessage(channel, EmbedUtil.play(translate("phrases.now"), String.format(translate("phrases.now.playing"), track.getInfo().title), track.getDuration()));
                break;
            case ERROR:
                SafeMessage.sendMessage(channel, info(translate("phrases.error"), String.format(translate("phrases.loadfailed"), String.format("[%s](%s)", track.getInfo().title, track.getInfo().uri))));
                break;
            case NOT_FOUND:
                SafeMessage.sendMessage(channel, info(translate("phrases.error"), String.format(translate("phrases.searching.nomatches"), String.format("[%s](%s)", track.getInfo().title, track.getInfo().uri))));
                break;
            case NULL:
                SafeMessage.sendMessage(channel, info(translate("phrases.error"), translate("phrases.loadfailed.null")));
                break;
            case LOCAL_SONGS:
                SafeMessage.sendMessage(channel, info(translate("phrases.warning"), String.format(translate("phrases.loadskipped.local"), Integer.valueOf(track.getInfo().title))));
                break;
            default:
                break;
        }
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
        latestEvent = event;
        guild = event.getGuild();

        CommandLine args;
        try {
            args = event.asCli(CLI_OPTIONS);
        } catch (ParseException e) {
            SafeMessage.sendMessage(event.getChannel(), error(event.translate("phrases.error"), String.format(event.translate("phrases.error.cli"), e.getMessage(), FormatUtil.formatHelp("play [options] <url/search>", CLI_OPTIONS))));
            return;
        }

        UserPermissions userPermissions = EntityProvider.getUser(event.getAuthor().getIdLong()).getPermissions();
        Permissions tierTwo = Permissions.tierTwo();

        if (trackQueue.size() >= Constants.QUEUE_LENGTH && !tierTwo.isCovered(userPermissions, event)) {
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.error"), event.translate("phrases.premium.queuefull")).setFooter(translate("phrases.premium.footer"), null));
            return;
        }

        String keyword = String.join(" ", args.getArgs());

        AtomicBoolean isUrl = new AtomicBoolean(true);

        final boolean isSoundcloud = args.hasOption("sc");
        final boolean isForce = args.hasOption("f");
        final boolean isTop = args.hasOption("t");

        Message infoMessage = SafeMessage.sendMessageBlocking(event.getChannel(), info(event.translate("phrases.searching"), String.format(event.translate("phrases.searching.description"), keyword)));

        if (!keyword.startsWith("http://") && !keyword.startsWith("https://")) {
            if (isSoundcloud) keyword = "scsearch: " + keyword;
            else keyword = "ytsearch: " + keyword;
            isUrl.set(false);
        }

        if (keyword.matches("(https?://)?(.*)?spotify\\.com.*") || keyword.matches("(https?://)?(.*)?deezer\\.com.*") || keyword.matches("(https?://)?itunes\\.apple\\.com.*"))
            keyword = removeQueryFromUrl(keyword);

        GroovyBot.getInstance().getLavalinkManager().getAudioPlayerManager().source(SpotifySourceManager.class).setPlayer(this);

        getAudioPlayerManager().loadItem(keyword, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                queueWithChecks(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                List<AudioTrack> tracks = audioPlaylist.getTracks();

                if (tracks.isEmpty()) {
                    SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.error"), event.translate("phrases.nothingfound")));
                    return;
                }

                if (!tierTwo.isCovered(userPermissions, event))
                    tracks = tracks.stream()
                            .limit(Constants.QUEUE_LENGTH - getQueueSize())
                            .collect(Collectors.toList());

                if (tracks.isEmpty()) {
                    SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.error"), event.translate("phrases.premium.queuefull")));
                    return;
                }

                if (!tierTwo.isCovered(userPermissions, event))
                    tracks = tracks.stream()
                            .filter(track -> track.getDuration() < Constants.SONG_DURATION)
                            .collect(Collectors.toList());

                if (tracks.isEmpty()) {
                    SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.error"), event.translate("phrases.premium.songduration")));
                    return;
                }

                List<AudioTrack> duplicates = new ArrayList<>();

                if (isUrl.get()) {
                    if (EntityProvider.getGuild(guild.getIdLong()).isPreventDups()) {
                        List<AudioTrack> filtered = new ArrayList<>();
                        tracks.forEach(t -> {
                            if (!checkDups(t))
                                filtered.add(t);
                            else
                                duplicates.add(t);
                        });
                        tracks.clear();
                        tracks.addAll(filtered);
                    }

                    queueTracks(tracks.toArray(new AudioTrack[0]));

                    if (!duplicates.isEmpty())
                        SafeMessage.editMessage(infoMessage, success(event.translate("phrases.loaded"), String.format(event.translate("phrases.loaded.playlist.duplicates"), tracks.size(), audioPlaylist.getName(), duplicates.size(), EntityProvider.getGuild(guild.getIdLong()).getPrefix())));
                    else
                        SafeMessage.editMessage(infoMessage, success(event.translate("phrases.loaded"), String.format(event.translate("phrases.loaded.playlist"), tracks.size(), audioPlaylist.getName())));

                    return;
                }

                if (EntityProvider.getGuild(guild.getIdLong()).isSearchPlay()) {
                    tracks = tracks.stream().limit(5).collect(Collectors.toList());
                    Message infoMessage = SafeMessage.sendMessageBlocking(event.getChannel(), info(event.translate("phrases.results"), SearchCommand.buildTrackDescription(tracks)));

                    for (int i = 0; i < tracks.size(); i++) {
                        infoMessage.addReaction(SearchCommand.EMOTES[i]).complete();
                    }

                    try {
                        new SearchCommand.MusicResult(infoMessage, event.getChannel(), event.getMember(), tracks, GroovyBot.getInstance().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel()));
                    } catch (InsufficientPermissionException e) {
                        sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.nopermission"), event.translate("phrases.nopermission.manage")));
                    }
                } else {
                    queueWithChecks(tracks.get(0));
                }
            }

            private void queueWithChecks(AudioTrack track) {
                if (track.getDuration() > Constants.SONG_DURATION && !Permissions.tierTwo().isCovered(userPermissions, event)) {
                    SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.premium"), event.translate("phrases.premium.songduration")).setFooter(event.translate("phrases.premium.footer"), null));
                    return;
                }

                if (checkDups(track)) {
                    SafeMessage.editMessage(infoMessage, info(event.translate("phrases.warning"), String.format(event.translate("phrases.duplicates.single"), EntityProvider.getGuild(guild.getIdLong()).getPrefix())));
                    return;
                }

                queueTrack(track, isForce, isTop);

                if (track.getInfo().isStream) {
                    SafeMessage.editMessage(infoMessage, EmbedUtil.success(event.translate("phrases.loaded"), String.format(event.translate("phrases.loaded.stream"), track.getInfo().title)));
                } else {
                    SafeMessage.editMessage(infoMessage,
                            EmbedUtil.success(event.translate("phrases.loaded"),
                                    String.format(event.translate("phrases.loaded.track"),
                                            track.getInfo().title)).setFooter(String.format("%s: %s", translate("phrases.estimated"),
                                    getQueueLengthMillis() == 0 ? "Now!" : FormatUtil.formatDuration(getQueueLengthMillis())), null));
                }
            }

            @Override
            public void noMatches() {
                SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.nothingfound"), event.translate("phrases.searching.nomatches")));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                SafeMessage.editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.error"), e.getCause() != null ? String.format("%s%n%s", e.getMessage(), e.getCause().getMessage()) : String.format("%s", e.getMessage())));
            }
        });
    }

    public void update() {
        if (channel.canTalk())
            SafeMessage.sendMessage(channel, EmbedUtil.small(translate("phrases.updating")));

        if (!isPlaying() || voiceChannel == null)
            return;

        // guild_id, current_track, current_position, queue, channel_id, text_channel_id, volume, bass_boost, skip_votes, loop_queue, loop, shuffle, auto_play

        try (Connection connection = GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO queues (guild_id, current_track, current_position, queue, channel_id, text_channel_id, volume, bassboost, skip_votes, loop_queue, loop, shuffle, auto_play) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setLong(1, guild.getIdLong());
            ps.setString(2, LavalinkUtil.toMessage(player.getPlayingTrack()));
            ps.setLong(3, player.getTrackPosition());
            ps.setString(4, getBuildedQueue());
            ps.setLong(5, voiceChannel.getIdLong());
            ps.setLong(6, channel.getIdLong());
            ps.setInt(7, player.getVolume());
            ps.setString(8, bassboost);
            ps.setInt(9, skipVotes);
            ps.setBoolean(10, getScheduler().isLoopqueue());
            ps.setBoolean(11, getScheduler().isLoop());
            ps.setBoolean(12, getScheduler().isShuffle());
            ps.setBoolean(13, getScheduler().isAutoPlay());
            ps.execute();
        } catch (SQLException | IOException e) {
            SafeMessage.sendMessage(channel, EmbedUtil.error(translate("phrases.error"), translate("phrases.updating.error")).setFooter(translate("phrases.redirect.to.devs"), null));
            log.error("[MusicPlayer] Error while updating!", e);
        }

        leave();
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
        if (event.getMember().equals(event.getGuild().getSelfMember())) {
            voiceChannel = event.getChannelJoined();
        }
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

    @Override
    public String translate(String key) {
        if (latestEvent == null)
            return GroovyBot.getInstance().getTranslationManager().getDefaultLocale().translate(key);
        return latestEvent.translate(key);
    }

    @Override
    protected void save() {
        GroovyBot.getInstance().getMusicPlayerManager().update(guild, this);
    }

    @Getter
    @RequiredArgsConstructor
    public enum VoteSkipReason {
        ALLOWED(null, null),
        ALONE("phrases.skipped", "command.skip"),
        DJ_IN_CHANNEL("phrases.nopermission", "command.voteskip.dj"),
        ERROR("phrases.error", "phrases.error.internal");

        private final String titleTranslationKey;
        private final String descriptionTranslationKey;
    }
}
