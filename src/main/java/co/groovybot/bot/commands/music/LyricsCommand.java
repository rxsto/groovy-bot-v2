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
import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.lyrics.GeniusClient;
import co.groovybot.bot.util.Colors;
import co.groovybot.bot.util.FormatUtil;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
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
                    editMessage(infoMessage, error(event.translate("phrases.nothingfound"), event.translate("command.lyrics.notfound")));
                else {
                    editMessage(infoMessage, getLyricsEmbed(geniusClient, lyricsUrl));
                }
            }
        else {
            if (args.length == 0) {
                Message infoMessage = sendMessageBlocking(event.getChannel(), info(event.translate("command.lyrics.searching.title"), event.translate("command.lyrics.searching.description")));
                String lyricsUrl = getLyricsUrl(player.getPlayer().getPlayingTrack().getInfo().title, geniusClient);

                if (lyricsUrl == null)
                    editMessage(infoMessage, error(event.translate("phrases.nothingfound"), event.translate("command.lyrics.notfound")));
                else
                    editMessage(infoMessage, getLyricsEmbed(geniusClient, lyricsUrl, player.getPlayer().getPlayingTrack().getInfo().title));
            } else {
                Message infoMessage = sendMessageBlocking(event.getChannel(), info(event.translate("command.lyrics.searching.title"), event.translate("command.lyrics.searching.description")));
                String lyricsUrl = getLyricsUrl(String.join(" ", args), geniusClient);

                if (lyricsUrl == null)
                    editMessage(infoMessage, error(event.translate("phrases.nothingfound"), event.translate("command.lyrics.notfound")));
                else
                    editMessage(infoMessage, getLyricsEmbed(geniusClient, lyricsUrl));
            }
        }
        return null;
    }

    private EmbedBuilder getLyricsEmbed(GeniusClient geniusClient, String lyricsUrl, String title) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(title, lyricsUrl).setColor(Colors.DARK_BUT_NOT_BLACK);
        String[] comps = getLyrics(lyricsUrl, geniusClient);
        String[] tempLine = new String[2];
        tempLine[0] = null;
        tempLine[1] = null;
        int count = 0;
        for (String comp : comps) {
            if (count == 0 && !comp.startsWith("t:"))
                embedBuilder.setDescription(comp);
            else {
                if (comp.startsWith("t:")) {
                    comp = comp.replace("t:", "");
                    tempLine[0] = comp;
                } else {
                    if (count == 0) tempLine[0] = "\u200b";
                    tempLine[1] = comp;
                }
                if (tempLine[0] != null && tempLine[1] != null) {
                    embedBuilder.addField(tempLine[0], tempLine[1], false);
                    tempLine[0] = null;
                    tempLine[1] = null;
                }
            }
            count++;
        }
        return embedBuilder;
    }

    private EmbedBuilder getLyricsEmbed(GeniusClient geniusClient, String lyricsUrl) {
        return getLyricsEmbed(geniusClient, lyricsUrl, geniusClient.getTitle(lyricsUrl));
    }

    private String getLyricsUrl(String query, GeniusClient geniusClient) {
        return geniusClient.searchSong(query);
    }

    public String[] getLyrics(String lyricsUrl, GeniusClient geniusClient) {
        return FormatUtil.formatLyrics(geniusClient.getLyrics(lyricsUrl));
    }
}
