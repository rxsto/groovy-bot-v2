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

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.audio.Player;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.interaction.InteractableMessage;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SemiInChannelCommand;
import co.groovybot.bot.core.events.command.CommandFailEvent;
import co.groovybot.bot.util.EmbedUtil;
import co.groovybot.bot.util.FormatUtil;
import co.groovybot.bot.util.SafeMessage;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.utils.Helpers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
public class SearchCommand extends SemiInChannelCommand {

    public static final String[] EMOTES = {"\u0031\u20E3", "\u0032\u20E3", "\u0033\u20E3", "\u0034\u20E3", "\u0035\u20E3"};
    private final SearchCommand instance;
    private Message infoMessage;

    public SearchCommand() {
        super(new String[]{"search", "find"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets you search for songs", "<song>");
        instance = this;
    }

    public static String buildTrackDescription(List<AudioTrack> results) {
        final String[] NUMBERS = {"`1.`", "`2.`", "`3.`", "`4.`", "`5.`", "`6.`"};

        StringBuilder resultBuilder = new StringBuilder();
        AtomicInteger count = new AtomicInteger(0);

        results.forEach(track -> {
            final AudioTrackInfo info = track.getInfo();
            resultBuilder.append(NUMBERS[count.getAndAdd(1)]).append(" [").append(info.title).append(" - ").append(info.author).append("](").append(info.uri).append(")").append("\n");
        });

        return resultBuilder.toString();
    }

    private static void removeReactions(Message message) {
        if (message.getGuild().getSelfMember().hasPermission(message.getTextChannel(), Permission.MESSAGE_MANAGE))
            message.clearReactions().queue();
    }

    @Override
    public Result executeCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0)
            return send(info(event.translate("phrases.error"), event.translate("phrases.error.noquery")));

        String keyword = "ytsearch: " + event.getArguments();

        player.getAudioPlayerManager().loadItem(keyword, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (track.getInfo().isStream)
                    SafeMessage.sendMessage(event.getChannel(), EmbedUtil.success(event.translate("phrases.loaded"), String.format(event.translate("phrases.loaded.stream"), track.getInfo().title)));
                else
                    SafeMessage.sendMessage(event.getChannel(), EmbedUtil.success(event.translate("phrases.loaded"), String.format(event.translate("phrases.loaded.track"), track.getInfo().title)).setFooter(String.format("Estimated: %s", player.getQueueLengthMillis() == 0 ? "Now!" : FormatUtil.formatDuration(player.getQueueLengthMillis())), null));

                player.play(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                List<AudioTrack> results = tracks.stream().limit(tracks.size() < 5 ? tracks.size() : 5).collect(Collectors.toList());
                infoMessage = sendMessageBlocking(event.getChannel(), info(event.translate("phrases.results"), buildTrackDescription(results)));

                for (int i = 0; i < results.size(); i++) {
                    infoMessage.addReaction(EMOTES[i]).complete();
                }

                try {
                    new MusicResult(infoMessage, event.getChannel(), event.getMember(), results, player);
                } catch (InsufficientPermissionException e) {
                    editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.nopermission"), event.translate("phrases.nopermission.manage")));
                    removeReactions(infoMessage);
                }
            }


            @Override
            public void noMatches() {
                editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.nothingfound"), event.translate("phrases.searching.nomatches")));
                removeReactions(infoMessage);
                leave();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                editMessage(infoMessage, error(event));
                removeReactions(infoMessage);
                event.getBot().getEventManager().handle(new CommandFailEvent(event, instance, exception));
                leave();
            }

            private void leave() {
                if (!player.isPlaying())
                    player.leave();
            }
        });

        return null;
    }

    public static class MusicResult extends InteractableMessage {

        private final List<AudioTrack> searchResults;
        private final Player player;

        public MusicResult(Message infoMessage, TextChannel channel, Member author, List<AudioTrack> searchResults, Player player) {
            super(infoMessage, channel, author, author.getUser().getIdLong());
            this.searchResults = searchResults;
            this.player = player;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    unregister();
                }
            }, 15 * 1000);
        }

        @Override
        protected void handleMessage(GuildMessageReceivedEvent event) {
            final String contentRaw = event.getMessage().getContentRaw();
            final User author = event.getAuthor();

            if (!Helpers.isNumeric(contentRaw)) {
                editMessage(getInfoMessage(), error(translate(author, "phrases.invalid"), translate(author, "phrases.invalid.number")));
                unregister();
                return;
            }

            int song = Integer.parseInt(contentRaw);

            if (song > 5 || (song - 1) > searchResults.size()) {
                editMessage(getInfoMessage(), error(translate(author, "phrases.invalid"), translate(author, "phrases.invalid.number")));
                unregister();
                return;
            }

            AudioTrack track = searchResults.get(song - 1);
            player.queueTrack(track, false, false);

            if (track.getInfo().isStream)
                SafeMessage.editMessage(getInfoMessage(), EmbedUtil.success(translate(author, "phrases.loaded"), String.format(translate(author, "phrases.loaded.stream"), track.getInfo().title)));
            else
                SafeMessage.editMessage(getInfoMessage(), EmbedUtil.success(translate(author, "phrases.loaded"), String.format(translate(author, "phrases.loaded.track"), track.getInfo().title)).setFooter(String.format("Estimated: %s", player.getQueueLengthMillis() == 0 ? "Now!" : FormatUtil.formatDuration(player.getQueueLengthMillis())), null));

            unregister();
        }

        @Override
        protected void handleReaction(GuildMessageReactionAddEvent event) {
            final User author = event.getUser();
            final String reactionRaw = event.getReactionEmote().getName();

            int song = 0;

            switch (reactionRaw) {
                case "\u0031\u20E3":
                    song = 1;
                    break;
                case "\u0032\u20E3":
                    song = 2;
                    break;
                case "\u0033\u20E3":
                    song = 3;
                    break;
                case "\u0034\u20E3":
                    song = 4;
                    break;
                case "\u0035\u20E3":
                    song = 5;
                    break;
                default:
                    editMessage(getInfoMessage(), error(translate(event.getUser(), "phrases.invalid"), translate(event.getUser(), "phrases.invalid.number")));
            }

            if (song - 1 > searchResults.size()) {
                editMessage(getInfoMessage(), error(translate(event.getUser(), "phrases.invalid"), translate(event.getUser(), "phrases.invalid.number")));
                unregister();
                return;
            }

            AudioTrack track = searchResults.get(song - 1);
            player.queueTrack(track, false, false);

            if (track.getInfo().isStream)
                SafeMessage.editMessage(getInfoMessage(), EmbedUtil.success(translate(author, "phrases.loaded"), String.format(translate(author, "phrases.loaded.stream"), track.getInfo().title)));
            else
                SafeMessage.editMessage(getInfoMessage(), EmbedUtil.success(translate(author, "phrases.loaded"), String.format(translate(author, "phrases.loaded.track"), track.getInfo().title)).setFooter(String.format("Estimated: %s", player.getQueueLengthMillis() == 0 ? "Now!" : FormatUtil.formatDuration(player.getQueueLengthMillis())), null));

            unregister();
        }
    }
}
