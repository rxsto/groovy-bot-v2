package io.groovybot.bot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.lyrics.GeniusClient;
import lombok.extern.log4j.Log4j;
import net.dv8tion.jda.core.entities.Message;

import java.io.IOException;

@Log4j
public class LyricsCommand extends Command {

    public LyricsCommand() {
        super(new String[] {"lyrics", "lyric", "songtext"}, CommandCategory.MUSIC, Permissions.everyone(), "Provieds you the lyrics of your current song", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        MusicPlayer player = event.getGroovyBot().getMusicPlayerManager().getPlayer(event);
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        Message infoMessage = sendMessageBlocking(event.getChannel(), info(event.translate("command.lyrics.searching.title"), event.translate("command.lyrics.searching.description")));
        final GeniusClient geniusClient = event.getGroovyBot().getGeniusClient();
        final AudioTrackInfo info = player.getPlayer().getPlayingTrack().getInfo();
        final String title = info.title;
        String lyricsUrl = geniusClient.searchSong(title);
        if (lyricsUrl.equals("")) {
            infoMessage.editMessage(error(event.translate("command.lyrics.notfound.title"), event.translate("command.lyrics.notfound.description")).build()).queue();
            return null;
        }
        infoMessage.editMessage(info(event.translate("command.lyrics.crawling.title"), event.translate("command.lyrics.crawling.description")).build()).queue();
        String lyrics;
        try {
            lyrics = geniusClient.findLyrics(lyricsUrl);
        } catch (IOException e) {
            infoMessage.editMessage(error(event).build()).queue();
            log.error("[Genius] An error occurred while crawling lyrics", e);
            return null;
        }
        infoMessage.editMessage(info("", String.format(event.translate("command.lyrics.success.description"), lyrics))
                .setTitle(String.format(event.translate("command.lyrics.success.title"), title), lyricsUrl)
                .build()).queue();
        return null;
    }
}
