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
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.utils.Helpers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SearchCommand extends SemiInChannelCommand {

    private final SearchCommand instance;

    public SearchCommand() {
        super(new String[]{"search", "find"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets you search for songs", "<song>");
        instance = this;
    }


    @Override
    public Result executeCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0)
            return send(info(event.translate("phrases.noquery.title"), event.translate("phrases.noquery.description")));
        String keyword = "ytsearch: " + event.getArguments();
        player.getAudioPlayerManager().loadItem(keyword, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                sendMessage(event.getChannel(), EmbedUtil.success(event.translate("phrases.searching.trackloaded.title"), String.format(event.translate("command.search.oneresult.description"), track.getInfo().title)));
                player.play(track, false);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                List<AudioTrack> results = tracks.stream().limit(tracks.size() < 5 ? tracks.size() : 5).collect(Collectors.toList());
                Message infoMessage = sendMessageBlocking(event.getChannel(), info(event.translate("command.search.results.title"), buildTrackDescription(results)).setFooter(event.translate("command.search.results.footer"), null));
                new MusicResult(infoMessage, event.getChannel(), event.getMember(), results, player);
            }


            @Override
            public void noMatches() {
                sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.searching.nomatches.title"), event.translate("phrases.searching.nomatches.description")), 10);
                leave();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                sendMessage(event.getChannel(), error(event), 10);
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

    private String buildTrackDescription(List<AudioTrack> results) {
        final String[] NUMBERS = {"**1:**", "**2:**", "**3:**", "**4:**", "**5:**", "**6:**"};
        StringBuilder resultBuilder = new StringBuilder();
        AtomicInteger count = new AtomicInteger(0);
        results.forEach(track -> {
            final AudioTrackInfo info = track.getInfo();
            resultBuilder.append(NUMBERS[count.getAndAdd(1)]).append(" [").append(info.title).append(" - ").append(info.author).append("](").append(info.uri).append(")").append("\n");
        });
        return resultBuilder.toString();
    }

    private class MusicResult extends InteractableMessage {

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
                sendMessage(event.getChannel(), error(translate(author, "phrases.invalidnumber.title"), translate(author, "phrases.invalidnumber.description")), 8);
                unregister();
                return;
            }
            int song = Integer.parseInt(contentRaw);
            if (song > 5 || (song - 1) > searchResults.size()) {
                sendMessage(event.getChannel(), error(translate(author, "phrases.invalidnumber.title"), translate(author, "phrases.invalidnumber.description")), 8);
                unregister();
                return;
            }
            AudioTrack track = searchResults.get(song - 1);
            player.queueTrack(track, false, false);
            sendMessage(event.getChannel(), EmbedUtil.success(translate(author, "phrases.searching.trackloaded.title"), String.format(translate(author, "phrases.searching.trackloaded.description"), track.getInfo().title)));
            unregister();
        }
    }
}
