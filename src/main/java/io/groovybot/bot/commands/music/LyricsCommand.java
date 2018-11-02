package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.lyrics.GeniusClient;
import io.groovybot.bot.util.FormatUtil;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.Message;

@Log4j2
public class LyricsCommand extends Command {

    public LyricsCommand() {
        super(new String[]{"lyrics", "ly"}, CommandCategory.MUSIC, Permissions.everyone(), "Provides you lyrics from either the current song or the given query", "[query]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        MusicPlayer player = event.getBot().getMusicPlayerManager().getPlayer(event);
        GeniusClient geniusClient = event.getBot().getGeniusClient();

        if (!player.isPlaying())
            if (args.length == 0)
                return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
            else {
                Message infoMessage = sendMessageBlocking(event.getChannel(), info(event.translate("command.lyrics.searching.title"), event.translate("command.lyrics.searching.description")));
                String lyricsUrl = getLyricsUrl(String.join(" ", args), geniusClient);

                if (lyricsUrl == null)
                    editMessage(infoMessage, error(event.translate("command.lyrics.notfound.title"), event.translate("command.lyrics.notfound.description")));
                else {
                    editMessage(infoMessage, info(event.translate("command.lyrics.found.title"), getLyrics(lyricsUrl, geniusClient)).setTitle("\uD83D\uDCC4 " + geniusClient.getTitle(lyricsUrl), lyricsUrl));
                }
            }
        else {
            if (args.length == 0) {
                Message infoMessage = sendMessageBlocking(event.getChannel(), info(event.translate("command.lyrics.searching.title"), event.translate("command.lyrics.searching.description")));
                String lyricsUrl = getLyricsUrl(player.getPlayer().getPlayingTrack().getInfo().title, geniusClient);

                if (lyricsUrl == null)
                    editMessage(infoMessage, error(event.translate("command.lyrics.notfound.title"), event.translate("command.lyrics.notfound.description")));
                else {
                    editMessage(infoMessage, info(event.translate("command.lyrics.found.title"), getLyrics(lyricsUrl, geniusClient)).setTitle("\uD83D\uDCC4 " + player.getPlayer().getPlayingTrack().getInfo().title, lyricsUrl));
                }
            } else {
                Message infoMessage = sendMessageBlocking(event.getChannel(), info(event.translate("command.lyrics.searching.title"), event.translate("command.lyrics.searching.description")));
                String lyricsUrl = getLyricsUrl(String.join(" ", args), geniusClient);

                if (lyricsUrl == null)
                    editMessage(infoMessage, error(event.translate("command.lyrics.notfound.title"), event.translate("command.lyrics.notfound.description")));
                else {
                    editMessage(infoMessage, info(event.translate("command.lyrics.found.title"), getLyrics(lyricsUrl, geniusClient)).setTitle("\uD83D\uDCC4 " + geniusClient.getTitle(lyricsUrl), lyricsUrl));
                }
            }
        }
        return null;
    }

    private String getLyricsUrl(String query, GeniusClient geniusClient) {
        return geniusClient.searchSong(query);
    }

    public String getLyrics(String lyricsUrl, GeniusClient geniusClient) {
        return FormatUtil.formatLyrics(geniusClient.getLyrics(lyricsUrl));
    }
}
