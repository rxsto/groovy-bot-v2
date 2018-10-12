package io.groovybot.bot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.lyrics.GeniusClient;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.Message;

import java.io.IOException;

@Log4j2
public class LyricsCommand extends Command {

    public LyricsCommand() {
        super(new String[]{"lyrics", "lyric", "songtext"}, CommandCategory.MUSIC, Permissions.everyone(), "Provieds you the lyrics of your current song", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        MusicPlayer player = event.getBot().getMusicPlayerManager().getPlayer(event);
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        Message infoMessage = sendMessageBlocking(event.getChannel(), info(event.translate("command.lyrics.searching.title"), event.translate("command.lyrics.searching.description")));
        final GeniusClient geniusClient = event.getBot().getGeniusClient();
        final AudioTrackInfo info = player.getPlayer().getPlayingTrack().getInfo();
        final String title = info.title;
        String lyricsUrl = geniusClient.searchSong(title);
        if (lyricsUrl.equals("")) {
            editMessage(infoMessage, error(event.translate("command.lyrics.notfound.title"), event.translate("command.lyrics.notfound.description")));
            return null;
        }
        editMessage(infoMessage, info(event.translate("command.lyrics.crawling.title"), event.translate("command.lyrics.crawling.description")));
        String lyrics;
        try {
            lyrics = geniusClient.findLyrics(lyricsUrl);
        } catch (IOException e) {
            editMessage(infoMessage, error(event));
            log.error("[Genius] An error occurred while crawling lyrics", e);
            return null;
        }

        System.out.println(lyrics.length());

        if (lyrics.length() > 2000) {
            editMessage(infoMessage, info("", String.format(event.translate("command.lyrics.success.description"), lyrics.substring(0, 2000)))
                    .setTitle(String.format(event.translate("command.lyrics.success.title"), title), lyricsUrl)
            );

            if (lyrics.length() > 4000) {
                sendMessageBlocking(event.getChannel(), standard("Lyrics", lyrics.substring(2001, 4000)).setTitle(String.format(event.translate("command.lyrics.success.title"), title), lyricsUrl));
                sendMessageBlocking(event.getChannel(), standard("Lyrics", lyrics.substring(4001)).setTitle(String.format(event.translate("command.lyrics.success.title"), title), lyricsUrl));
            } else {
                sendMessageBlocking(event.getChannel(), standard("Lyrics", lyrics.substring(2001)).setTitle(String.format(event.translate("command.lyrics.success.title"), title), lyricsUrl));
            }
        } else {
            editMessage(infoMessage, info("", String.format(event.translate("command.lyrics.success.description"), lyrics))
                    .setTitle(String.format(event.translate("command.lyrics.success.title"), title), lyricsUrl)
            );
            System.out.println("<2000");
        }
        return null;
    }
}
