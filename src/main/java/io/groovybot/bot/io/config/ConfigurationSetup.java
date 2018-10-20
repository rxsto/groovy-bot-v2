package io.groovybot.bot.io.config;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConfigurationSetup {

    public static Configuration setupConfig() {
        Configuration configuration = new Configuration("config/config.json");
        final JSONObject botObject = new JSONObject();
        botObject.put("token", "defaultvalue");
        configuration.addDefault("bot", botObject);
        final JSONObject dbObject = new JSONObject();
        dbObject.put("host", "defaultvalue");
        dbObject.put("port", "defaultvalue");
        dbObject.put("database", "defaultvalue");
        dbObject.put("username", "defaultvalue");
        dbObject.put("password", "defaultvalue");
        configuration.addDefault("db", dbObject);
        final JSONObject wsObject = new JSONObject();
        wsObject.put("host", "defaultvalue");
        wsObject.put("port", "defaultvalue");
        wsObject.put("token", "defaultvalue");
        configuration.addDefault("websocket", wsObject);
        final JSONArray gamesArray = new JSONArray();
        gamesArray.put("gamename");
        configuration.addDefault("games", gamesArray);
        final JSONObject settingsObject = new JSONObject();
        settingsObject.put("prefix", "g!");
        settingsObject.put("test_prefix", "gt!");
        settingsObject.put("maxShards", 5);
        configuration.addDefault("settings", settingsObject);
        final JSONArray ownersArray = new JSONArray();
        ownersArray.put(264048760580079616L);
        ownersArray.put(254892085000405004L);
        configuration.addDefault("owners", ownersArray);
        final JSONObject webhookObject = new JSONObject();
        webhookObject.put("error_hook", "http://hook.com");
        configuration.addDefault("webhooks", webhookObject);
        final JSONObject youtubeObject = new JSONObject();
        youtubeObject.put("apikey", "defaultvalue");
        configuration.addDefault("youtube", youtubeObject);
        final JSONObject spotifyObject = new JSONObject();
        spotifyObject.put("client_id", "defaultvalue");
        spotifyObject.put("client_token", "defaultvalue");
        configuration.addDefault("spotify", spotifyObject);
        final JSONObject botlistObjects = new JSONObject()
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
        configuration.addDefault("botlists", botlistObjects);
        final JSONObject statusPageObject = new JSONObject();
        statusPageObject.put("page_id", "defaultvalue");
        statusPageObject.put("metric_id", "defaultvalue");
        statusPageObject.put("api_key", "defaultvalue");
        configuration.addDefault("statuspage", statusPageObject);
        final JSONObject geniusObject = new JSONObject();
        geniusObject.put("token", "defaultvalue");
        configuration.addDefault("genius", geniusObject);
        return configuration;
    }
}
