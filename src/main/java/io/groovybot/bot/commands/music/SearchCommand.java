package io.groovybot.bot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.audio.Player;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.interaction.InteractableMessage;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SemiInChannelCommand;
import io.groovybot.bot.core.events.command.CommandFailEvent;
import io.groovybot.bot.util.EmbedUtil;
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
        super(new String[] {"search", "find"}, CommandCategory.MUSIC, Permissions.djMode(), "Lets you search songs", "<song>");
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
                sendMessage(event.getChannel(), EmbedUtil.success(event.translate("phrases.searching.trackloaded.title"), String.format(event.translate("commands.search.oneresult.description"), track.getInfo().title)));
                player.play(track);
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
                event.getGroovyBot().getEventManager().handle(new CommandFailEvent(event, instance,  exception));
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
        final String[] NUMBERS = {":one:", ":two:", ":three:", ":four:", ":five:", ":six:"};
        StringBuilder resultBuilder = new StringBuilder();
        AtomicInteger count = new AtomicInteger(0);
        results.forEach(track -> {
            final AudioTrackInfo info = track.getInfo();
            resultBuilder.append(NUMBERS[count.getAndAdd(1)]).append(" - [").append(info.title).append(" - ").append(info.author).append("](").append(info.uri).append(")").append("\n");
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
                sendMessage(event.getChannel(), error(translate(author, "commands.search.invalidnumber.title"), translate(author, "commands.search.invalidnumber.description")), 8);
                unregister();
                return;
            }
            AudioTrack track = searchResults.get(song - 1);
            player.queueTrack(track, false);
            sendMessage(event.getChannel(), EmbedUtil.success(translate(author, "phrases.searching.trackloaded.title"), String.format(translate(author, "phrases.searching.trackloaded.description"), track.getInfo().title)));
            unregister();
        }
    }
}
