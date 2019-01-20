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

package co.groovybot.bot.core;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.LavalinkManager;
import co.groovybot.bot.util.NameThreadFactory;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Log4j2
public class GameAnimator implements Runnable {

    private final ShardManager shardManager;
    private final GroovyBot groovyBot;
    private Game[] games;

    public GameAnimator(GroovyBot groovyBot) {
        log.info("[GameAnimator] Initializing GameAnimator ...");
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new NameThreadFactory("GameAnimator"));
        List<Game> gameList = new ArrayList<>();
        this.groovyBot = groovyBot;
        this.shardManager = groovyBot.getShardManager();
        groovyBot.getConfig().getJSONArray("games").forEach(game -> gameList.add(parseGame(String.valueOf(game))));
        this.games = gameList.toArray(new Game[0]);
        shardManager.setStatus(OnlineStatus.ONLINE);
        scheduler.scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);
        log.info("[GameAnimator] Successfully initialized GameAnimator!");
    }

    @Override
    public void run() {
        shardManager.setGame(games[ThreadLocalRandom.current().nextInt(games.length)]);
    }

    private Game parseGame(String game) {
        String preparesString = parsePlaceholders(game);
        if (game.startsWith("p: "))
            return Game.playing(preparesString.replaceFirst("p: ", ""));
        else if (game.startsWith("l: "))
            return Game.listening(preparesString.replaceFirst("l: ", ""));
        else if (game.startsWith("s: "))
            return Game.streaming(preparesString.replaceFirst("s: ", ""), "https://twitch.tv/groovydevs");
        else if (game.startsWith("w: "))
            return Game.watching(preparesString.replaceFirst("w: ", ""));
        return null;
    }

    private String parsePlaceholders(String game) {
        return game.replace("%channels%", String.valueOf(LavalinkManager.countPlayers())).replace("%guilds%", String.valueOf(shardManager.getGuilds().size())).replace("%users%", String.valueOf(shardManager.getUsers().size())).replace("%shards%", String.valueOf(shardManager.getShardsTotal())).replace("%prefix%", groovyBot.getConfig().getJSONObject("settings").getString("prefix")).replace("%name%", groovyBot.getShardManager().getApplicationInfo().getJDA().getSelfUser().getName());
    }
}
