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

package co.groovybot.bot.commands.music;


import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.audio.Scheduler;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.interaction.InteractableMessage;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.util.*;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lavalink.client.player.IPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ControlCommand extends SameChannelCommand {

    private final String[] EMOTES = {"⏯", "⏭", "🔁", "🔀", "🔄", "🔉", "🔊"};

    public ControlCommand() {
        super(new String[]{"control", "panel", "cp"}, CommandCategory.MUSIC, Permissions.tierOne(), "Lets you control Groovy with reactions", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));

        if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_MANAGE))
            return send(EmbedUtil.error(event.translate("phrases.nopermission"), event.translate("phrases.nopermission.manage")));

        if (controlPanelExists(event.getGuild().getIdLong())) {
            Message confirmMessage = SafeMessage.sendMessageBlocking(event.getChannel(), EmbedUtil.info(event.translate("command.control.alreadyinuse.title"), event.translate("command.control.alreadyinuse.description")));
            confirmMessage.addReaction("✅").queue();
            confirmMessage.addReaction("❌").queue();
            event.getBot().getEventWaiter().waitForEvent(GuildMessageReactionAddEvent.class, e -> confirmMessage.getIdLong() == e.getMessageIdLong() && e.getGuild().equals(event.getGuild()) && !e.getUser().isBot(),
                    e -> {
                        if (e.getReactionEmote().getName().equals("✅")) {
                            ControlPanel panel = getControlPanel(event.getGuild().getIdLong());
                            if (!panel.isWhitelisted(e.getMember())) {
                                SafeMessage.sendMessage(e.getChannel(), EmbedUtil.error(event.translate("phrases.notsamechannel.title"), event.translate("phrases.notsamechannel.description")), 5);
                                return;
                            } else {
                                new ControlPanel(event, sendInfoMessage(event), event.getChannel(), event.getMember(), player);
                                panel.delete();
                            }
                        }
                        confirmMessage.delete().queue();
                    });
        } else
            new Thread(() -> new ControlPanel(event, sendInfoMessage(event), event.getChannel(), event.getMember(), player), "command.control").start();
        return null;
    }

    private Message sendInfoMessage(CommandEvent event) {
        return SafeMessage.sendMessageBlocking(event.getChannel(), EmbedUtil.small(event.translate("phrases.loading")));
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

        private final CommandEvent commandEvent;
        private final VoiceChannel channel;
        private final ScheduledExecutorService scheduler;
        private final MusicPlayer player;
        private boolean ready;

        public ControlPanel(CommandEvent commandEvent, Message infoMessage, TextChannel channel, Member author, MusicPlayer player) {
            super(infoMessage, channel, author, infoMessage.getIdLong());
            this.commandEvent = commandEvent;
            this.channel = author.getGuild().getSelfMember().getVoiceState().getChannel();
            this.player = player;
            this.scheduler = Executors.newScheduledThreadPool(1, new NameThreadFactory("ControlCommand"));
            for (String emote : EMOTES) {
                JDAUtil.waitForEntity(getInfoMessage().addReaction(emote));
            }
            ready = true;
            run();
            scheduler.scheduleAtFixedRate(this, 0, 5, TimeUnit.SECONDS);
        }

        @Override
        protected void handleReaction(GuildMessageReactionAddEvent event) {
            if (!ready) return;
            if (!isWhitelisted(event.getMember())) return;
            final IPlayer musicPlayer = this.player.getPlayer();
            final Scheduler playerScheduler = this.player.getScheduler();
            switch (event.getReaction().getReactionEmote().getName()) {
                case "⏯":
                    if (!player.isPaused()) {
                        musicPlayer.setPaused(true);
                        this.player.getHandler().handleTrackPause();
                    } else {
                        musicPlayer.setPaused(false);
                        this.player.getHandler().handleTrackResume();
                    }
                    break;
                case "⏭":
                    this.player.skip();
                    break;
                case "\uD83D\uDD01":
                    if (!playerScheduler.isLoop() && !playerScheduler.isLoopqueue())
                        playerScheduler.setLoop(true);
                    else if (playerScheduler.isLoop()) {
                        if (Permissions.tierOne().isCovered(EntityProvider.getUser(this.commandEvent.getAuthor().getIdLong()).getPermissions(), this.commandEvent))
                            playerScheduler.setLoopqueue(true);
                        playerScheduler.setLoop(false);
                    } else if (playerScheduler.isLoopqueue())
                        playerScheduler.setLoopqueue(false);
                    break;
                case "\uD83D\uDD00":
                    if (!playerScheduler.isShuffle()) {
                        playerScheduler.setShuffle(true);
                    } else {
                        playerScheduler.setShuffle(false);
                    }
                    break;
                case "\uD83D\uDD0A":
                    if (musicPlayer.getVolume() == 200) {
                        return;
                    }
                    if (musicPlayer.getVolume() >= 190)
                        this.player.setVolume(200);
                    else
                        this.player.setVolume(musicPlayer.getVolume() + 10);
                    break;
                case "\uD83D\uDD09":
                    if (musicPlayer.getVolume() == 0) {
                        return;
                    }
                    if (musicPlayer.getVolume() <= 10)
                        this.player.setVolume(0);
                    else
                        this.player.setVolume(musicPlayer.getVolume() - 10);
                    break;
                case "\uD83D\uDD04":
                    player.seekTo(0);
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
            if (!player.isPlaying()) delete();
            if (player.getPlayer() == null || player.getPlayer().getPlayingTrack() == null) return;
            AudioTrackInfo currentSong = player.getPlayer().getPlayingTrack().getInfo();
            EmbedBuilder controlPanelEmbed = new EmbedBuilder()
                    .setTitle(commandEvent.translate("command.control.title"))
                    .setDescription(String.format("[%s](%s)", currentSong.title, currentSong.uri))
                    .setColor(Colors.DARK_BUT_NOT_BLACK)
                    .setFooter(buildControlInformation(player).toString(), null);
            SafeMessage.editMessage(getInfoMessage(), controlPanelEmbed);
        }

        private CharSequence buildControlInformation(MusicPlayer player) {
            final AudioTrack playingTrack = player.getPlayer().getPlayingTrack();
            final long trackPosition = player.getPlayer().getTrackPosition();
            return String.format("%s %s %s %s %s [%s] 🔊 %s", player.isPaused() ? "\u23F8" : "", player.loopEnabled() ? "\uD83D\uDD02" : "", player.loopQueueEnabled() ? "\uD83D\uDD01" : "", player.shuffleEnabled() ? "\uD83D\uDD00" : "", FormatUtil.formatProgressBar(trackPosition, playingTrack.getDuration()), playingTrack.getInfo().isStream ? commandEvent.translate("phrases.text.stream") : String.format("%s/%s", FormatUtil.formatTimestamp(trackPosition), FormatUtil.formatTimestamp(playingTrack.getDuration())), player.getPlayer().getVolume() + "%");
        }

        private void delete() {
            scheduler.shutdownNow();
            unregister();
            if (getInfoMessage() != null)
                if (commandEvent.getGroovyGuild().isDeleteMessages())
                    getInfoMessage().delete().queue();
        }

        @Override
        public void onDelete() {
            if (getInfoMessage() != null)
                if (commandEvent.getGroovyGuild().isDeleteMessages())
                    getInfoMessage().delete().queue();
            if (!scheduler.isShutdown())
                scheduler.shutdownNow();
        }
    }
}
