package io.groovybot.bot.commands.music;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.audio.Scheduler;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.interaction.InteractableMessage;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;
import io.groovybot.bot.util.*;
import lavalink.client.player.IPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ControlCommand extends SameChannelCommand {

    private final String[] EMOTES = {"⏯", "⏭", "🔂", "🔁", "🔀", "🔄", "🔉", "🔊"};

    public ControlCommand() {
        super(new String[]{"control", "panel", "cp"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you control the bot with reactions", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE))
            return send(error(event.translate("phrases.nopermission.title"), event.translate("phrases.nopermission.manage")));
        if (controlPanelExists(event.getGuild().getIdLong())) {
            Message confirmMessage = sendMessageBlocking(event.getChannel(), info(event.translate("command.control.alreadyinuse.title"), event.translate("command.control.alreadyinuse.description")));
            confirmMessage.addReaction("✅").queue();
            confirmMessage.addReaction("❌").queue();
            event.getGroovyBot().getEventWaiter().waitForEvent(GuildMessageReactionAddEvent.class, e -> confirmMessage.getIdLong() == e.getMessageIdLong() && e.getGuild().equals(event.getGuild()) && !e.getUser().isBot(),
                    e -> {
                        if (e.getReactionEmote().getName().equals("✅")) {
                            ControlPanel panel = getControlPanel(event.getGuild().getIdLong());
                            if (!panel.isWhitelisted(e.getMember())) {
                                sendMessage(e.getChannel(), error(event.translate("command.control.alreadyinuse.nopermission.title"), event.translate("command.control.alreadyinuse.nopermission.description")), 5);
                                return;
                            } else {
                                new Thread(() -> new ControlPanel(sendInfoMessage(event), event.getChannel(), event.getMember(), player), "ControlPanel").start();
                                panel.delete();
                            }
                        }
                        confirmMessage.delete().queue();
                    });
        } else
            new Thread(() -> new ControlPanel(sendInfoMessage(event), event.getChannel(), event.getMember(), player), "ControlPanel").start();
        return null;
    }

    private Message sendInfoMessage(CommandEvent event) {
        return sendMessageBlocking(event.getChannel(), info(event.translate("command.control.loading.title"), event.translate("command.control.loading.description")));
    }

    private List<InteractableMessage> getControlPanels() {
        return GroovyBot.getInstance().getInteractionManager().getInteractionStorage().values().stream().filter(entry -> entry instanceof ControlPanel).collect(Collectors.toList());
    }

    private boolean controlPanelExists(Long guildId) {
        return !getControlPanels().stream().filter(entry -> entry.getChannel().getGuild().getIdLong() == guildId).collect(Collectors.toList()).isEmpty();
    }

    private ControlPanel getControlPanel(Long guildId) {
        return (ControlPanel) getControlPanels().stream().filter(entry -> entry.getChannel().getGuild().getIdLong() == guildId).collect(Collectors.toList()).get(0);
    }

    private class ControlPanel extends InteractableMessage implements Runnable {

        private final VoiceChannel channel;
        private final ScheduledExecutorService scheduler;
        private final MusicPlayer player;

        public ControlPanel(Message infoMessage, TextChannel channel, Member author, MusicPlayer player) {
            super(infoMessage, channel, author, infoMessage.getIdLong());
            this.channel = author.getGuild().getSelfMember().getVoiceState().getChannel();
            this.player = player;
            this.scheduler = Executors.newScheduledThreadPool(1, new NameThreadFactory("ControlPanel"));
            for (String emote : EMOTES) {
                getInfoMessage().addReaction(emote).complete();
            }
            run();
            scheduler.scheduleAtFixedRate(this, 0, 5, TimeUnit.SECONDS);
        }

        @Override
        protected void handleReaction(GuildMessageReactionAddEvent event) {
            final User author = event.getUser();
            if (!isWhitelisted(event.getMember()))
                return;
            final IPlayer musicPlayer = this.player.getPlayer();
            final Scheduler playerScheduler = this.player.getScheduler();
            switch (event.getReaction().getReactionEmote().getName()) {
                case "⏯":
                    if (!player.isPaused()) {
                        musicPlayer.setPaused(true);
                        sendMessage(translate(author, "controlpanel.paused.title"), translate(author, "controlpanel.paused.description"));
                    } else {
                        musicPlayer.setPaused(false);
                        sendMessage(translate(author, "controlpanel.resumed.title"), translate(author, "controlpanel.resumed.description"));
                    }
                    break;
                case "⏭":
                    this.player.skip();
                    sendMessage(translate(author, "controlpanel.skipped.title"), translate(author, "controlpanel.skipped.description"));
                    break;
                case "\uD83D\uDD02":
                    if (playerScheduler.isQueueRepeating() || playerScheduler.isShuffle()) {
                        sendMessage(translate(author, "controlpanel.loop.shuffle.title"), translate(author, "controlpanel.loop.shuffle.description"));
                        break;
                    }
                    if (!playerScheduler.isRepeating()) {
                        playerScheduler.setRepeating(true);
                        sendMessage(translate(author, "controlpanel.repeating.enabled.title"), translate(author, "controlpanel.repeating.enabled.description"));
                    } else {
                        playerScheduler.setRepeating(false);
                        sendMessage(translate(author, "controlpanel.repeating.disabled.title"), translate(author, "controlpanel.repeating.disabled.description"));
                    }
                    break;
                case "\uD83D\uDD01":
                    if (playerScheduler.isRepeating() || playerScheduler.isShuffle()) {
                        sendMessage(translate(author, "controlpanel.loop.shuffle.title"), translate(author, "controlpanel.loop.shuffle.description"));
                        break;
                    }
                    if (!playerScheduler.isQueueRepeating()) {
                        playerScheduler.setQueueRepeating(true);
                        sendMessage(translate(author, "controlpanel.queuerepeating.enabled.title"), translate(author, "controlpanel.queuerepeating.enabled.description"));
                    } else {
                        playerScheduler.setQueueRepeating(false);
                        sendMessage(translate(author, "controlpanel.queuerepeating.disabled.title"), translate(author, "controlpanel.queuerepeating.disabled.description"));
                    }
                    break;
                case "\uD83D\uDD00":
                    if (playerScheduler.isRepeating() || playerScheduler.isQueueRepeating()) {
                        sendMessage(translate(author, "controlpanel.shuffle.loop.title"), translate(author, "controlpanel.shuffle.loop.description"));
                        break;
                    }
                    if (!playerScheduler.isShuffle()) {
                        playerScheduler.setShuffle(true);
                        sendMessage(translate(author, "controlpanel.shuffle.enabled.title"), translate(author, "controlpanel.shuffle.enabled.description"));
                    } else {
                        playerScheduler.setShuffle(false);
                        sendMessage(translate(author, "controlpanel.shuffle.disabled.title"), translate(author, "controlpanel.shuffle.disabled.description"));
                    }
                    break;
                case "\uD83D\uDD0A":
                    if (musicPlayer.getVolume() == 200) {
                        sendMessage(translate(author, "controlpanel.volume.tohigh.title"), translate(author, "controlpanel.volume.tohigh.description"));
                        return;
                    }
                    if (musicPlayer.getVolume() >= 190)
                        this.player.setVolume(200);
                    else
                        this.player.setVolume(musicPlayer.getVolume() + 10);
                    sendMessage(translate(author, "controlpanel.volume.increased.title"), String.format(translate(author, "controlpanel.volume.increased.description"), musicPlayer.getVolume()));
                    break;
                case "\uD83D\uDD09":
                    if (musicPlayer.getVolume() == 0) {
                        sendMessage(translate(author, "controlpanel.volume.tolow.title"), translate(author, "controlpanel.volume.tolow.description"));
                        return;
                    }
                    if (musicPlayer.getVolume() <= 10)
                        this.player.setVolume(0);
                    else
                        this.player.setVolume(musicPlayer.getVolume() - 10);
                    sendMessage(translate(author, "controlpanel.volume.decreased.title"), String.format(translate(author, "controlpanel.volume.decreased.description"), musicPlayer.getVolume()));
                    break;
                case "\uD83D\uDD04":
                    player.seekTo(0);
                    sendMessage(translate(author, "controlpanel.reset.title"), String.format(translate(author, "controlpanel.reset.description"), musicPlayer.getVolume()));
                    break;
                default:
                    break;
            }
            run();
        }

        private boolean isWhitelisted(Member member) {
            return channel.getMembers().contains(member);
        }

        @Override
        public void run() {
            if (!player.isPlaying())
                delete();
            if (player.getPlayer() == null || player.getPlayer().getPlayingTrack() == null)
                return;
            AudioTrackInfo currentSong = player.getPlayer().getPlayingTrack().getInfo();
            EmbedBuilder controlPanelEmbed = new EmbedBuilder()
                    .setTitle(String.format(":notes: %s (%s)", currentSong.title, currentSong.author))
                    .setColor(Colors.DARK_BUT_NOT_BLACK)
                    .setDescription(buildDescription(player));
            getInfoMessage().editMessage(controlPanelEmbed.build()).queue();
        }

        private CharSequence buildDescription(MusicPlayer player) {
            final AudioTrack playingTrack = player.getPlayer().getPlayingTrack();
            final long trackPosition = player.getPlayer().getTrackPosition();
            return String.format("%s %s %s %s %s **[%s/%s]**", player.isPaused() ? "\u23F8" : "\u25B6", player.loopEnabled() ? "\uD83D\uDD02" : "", player.queueLoopEnabled() ? "\uD83D\uDD01" : "", player.shuffleEnabled() ? "\uD83D\uDD00" : "", getProgressBar(trackPosition, playingTrack.getDuration()), FormatUtil.formatTimestamp(trackPosition), FormatUtil.formatTimestamp(playingTrack.getDuration()));
        }

        private void delete() {
            scheduler.shutdownNow();
            unregister();
            if (getInfoMessage() != null)
                getInfoMessage().delete().queue();
        }

        private void sendMessage(String title, String message) {
            SafeMessage.sendMessage(getChannel(), EmbedUtil.success(title, message), 3);
        }

        private String getProgressBar(long progress, long full) {
            double percentage = (double) progress / full;
            StringBuilder progressBar = new StringBuilder();
            for (int i = 0; i < 15; i++) {
                if ((int) (percentage * 15) == i)
                    progressBar.append("\uD83D\uDD18");
                else
                    progressBar.append("▬");
            }
            return progressBar.toString();
        }

        @Override
        public void onDelete() {
            if (getInfoMessage() != null)
                getInfoMessage().delete().queue();
            if (!scheduler.isShutdown())
                scheduler.shutdownNow();
        }

    }
}
