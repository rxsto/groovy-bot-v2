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
        final JSONObject bot = new JSONObject()
                .put("token", "defaultvalue")
                .put("instance", "dev");
        configuration.addDefault("bot", bot);

        // Create object for database
        final JSONObject db = new JSONObject()
                .put("host", "defaultvalue")
                .put("port", "defaultvalue")
                .put("database", "defaultvalue")
                .put("username", "defaultvalue")
                .put("password", "defaultvalue");
        configuration.addDefault("db", db);

        // Create object for websocket
        final JSONObject ws = new JSONObject()
                .put("host", "defaultvalue")
                .put("port", "defaultvalue");
        configuration.addDefault("websocket", ws);

        // Create array for lavalink nodes (f*ck schlabbbi)
        configuration.addDefault("lavalink_nodes", new JSONArray().put("name&&ws://host:2333&&passwort"));

        // Create array for games
        final JSONArray games = new JSONArray()
                .put("defaultvalue");
        configuration.addDefault("games", games);

        // Create object for settings
        final JSONObject settings = new JSONObject()
                .put("prefix", "g!")
                .put("shards", 10)
                .put("voice", "default");
        configuration.addDefault("settings", settings);

        // Create array for owners
        final JSONArray owners = new JSONArray()
                .put(264048760580079616L)
                .put(254892085000405004L)
                .put(306480135832338432L)
                .put(207500411907735552L)
                .put(227817074976751616L)
                .put(153507094933274624L);
        configuration.addDefault("owners", owners);

        // Create object for youtube
        final JSONObject youtube = new JSONObject()
                .put("apikey", "defaultvalue");
        configuration.addDefault("youtube", youtube);

        // Create object for kereru
        final JSONObject kereru = new JSONObject()
                .put("host", "http://127.0.0.1:1337/v1");
        configuration.addDefault("kereru", kereru);

        // Create object for genius
        final JSONObject genius = new JSONObject()
                .put("token", "defaultvalue");
        configuration.addDefault("genius", genius);

        // Create object for statuspage
        final JSONObject statuspage = new JSONObject()
                .put("page_id", "defaultvalue")
                .put("metric_id", "defaultvalue")
                .put("api_key", "defaultvalue");
        configuration.addDefault("statuspage", statuspage);

        // Create object for influxdb
        final JSONObject influxdb = new JSONObject()
                .put("username", "defaultvalue")
                .put("password", "defaultvalue")
                .put("host", "defaultvalue")
                .put("database", "defaultvalue");
        configuration.addDefault("influxdb", influxdb);

        // Create object for botlists
//        final JSONObject botlists = new JSONObject()
//                .put("DiscordBotsORG", "defaultvalue")
//                .put("BotlistSPACE", "defaultvalue")
//                .put("DiscordBotsGG", "defaultvalue");
//        configuration.addDefault("botlists", botlists);

        final JSONObject graylog = new JSONObject()
                .put("host", "localhost")
                .put("port", 12201);
        configuration.addDefault("graylog", graylog);

        final JSONObject voiceRss = new JSONObject()
                .put("api_key", "defaultvalue");
        configuration.addDefault("voice_rss", voiceRss);

        final JSONObject redis = new JSONObject()
                .put("host", "localhost")
                .put("password", "defaultvalue");
        configuration.addDefault("redis", redis);

        // Return config
        return configuration;
    }
}
