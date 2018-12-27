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

package co.groovybot.bot.io.config;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConfigurationSetup {

    public static Configuration setupConfig() {
        // Create new file
        Configuration configuration = new Configuration("config/config.json");

        // Create object for bot
        final JSONObject bot = new JSONObject();
        bot.put("token", "defaultvalue");
        bot.put("instance", "dev");
        configuration.addDefault("bot", bot);

        // Create object for database
        final JSONObject db = new JSONObject();
        db.put("host", "defaultvalue");
        db.put("port", "defaultvalue");
        db.put("database", "defaultvalue");
        db.put("username", "defaultvalue");
        db.put("password", "defaultvalue");
        configuration.addDefault("db", db);

        // Create object for websocket
        final JSONObject ws = new JSONObject();
        ws.put("host", "defaultvalue");
        ws.put("port", "defaultvalue");
        configuration.addDefault("websocket", ws);

        // Create array for lavalink nodes (f*ck schlabbbi)
        configuration.addDefault("lavalink_nodes", new JSONArray().put("name&&ws://host:2333&&passwort"));

        // Create array for games
        final JSONArray games = new JSONArray();
        games.put("defaultvalue");
        configuration.addDefault("games", games);

        // Create object for settings
        final JSONObject settings = new JSONObject();
        settings.put("prefix", "g!");
        settings.put("shards", 10);
        settings.put("voice", "default");
        configuration.addDefault("settings", settings);

        // Create array for owners
        final JSONArray owners = new JSONArray();
        owners.put(264048760580079616L);
        owners.put(254892085000405004L);
        owners.put(306480135832338432L);
        owners.put(207500411907735552L);
        owners.put(227817074976751616L);
        owners.put(153507094933274624L);
        configuration.addDefault("owners", owners);

        // Create object for youtube
        final JSONObject youtube = new JSONObject();
        youtube.put("apikey", "defaultvalue");
        configuration.addDefault("youtube", youtube);

        // Create object for spotify
        final JSONObject spotify = new JSONObject();
        spotify.put("client_id", "defaultvalue");
        spotify.put("client_secret", "defaultvalue");
        configuration.addDefault("spotify", spotify);

        // Create object for genius
        final JSONObject genius = new JSONObject();
        genius.put("token", "defaultvalue");
        configuration.addDefault("genius", genius);

        // Create object for statuspage
        final JSONObject statuspage = new JSONObject();
        statuspage.put("page_id", "defaultvalue");
        statuspage.put("metric_id", "defaultvalue");
        statuspage.put("api_key", "defaultvalue");
        configuration.addDefault("statuspage", statuspage);

        // Create object for influxdb
        final JSONObject influxdb = new JSONObject();
        influxdb.put("username", "defaultvalue");
        influxdb.put("password", "defaultvalue");
        influxdb.put("host", "defaultvalue");
        influxdb.put("database", "defaultvalue");
        configuration.addDefault("influxdb", influxdb);

        // Create object for botlists
        final JSONObject botlists = new JSONObject()
                .put("botlist.space", "defaultvalue")
                .put("bots.ondiscord.xyz", "defaultvalue")
                .put("discordboats.xyz", "defaultvalue")
                .put("discordboats.club", "defaultvalue")
                .put("discordbotlist.com", "defaultvalue")
                .put("discordbot.world", "defaultvalue")
                .put("bots.discord.pw", "defaultvalue")
                .put("discordbotlist.xyz", "defaultvalue")
                .put("discordbots.group", "defaultvalue")
                .put("bots.discordlist.app", "defaultvalue")
                .put("discord.services", "defaultvalue")
                .put("discordsbestbots.xyz", "defaultvalue")
                .put("divinediscordbots.com", "defaultvalue")
                .put("discordbotindex.com", "defaultvalue");
        configuration.addDefault("botlists", botlists);

        final JSONObject graylog = new JSONObject()
                .put("host", "localhost")
                .put("port", 12201);
        configuration.addDefault("graylog", graylog);

        final JSONObject voiceRss = new JSONObject()
                .put("api_key", "defaultvalue");
        configuration.addDefault("voice_rss", voiceRss);

        final JSONObject prometheus = new JSONObject()
                .put("port", 3033);
        configuration.addDefault("prometheus", prometheus);

        // Return config
        return configuration;
    }
}
