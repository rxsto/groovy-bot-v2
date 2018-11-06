package io.groovybot.bot.io.config;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConfigurationSetup {

    public static Configuration setupConfig() {
        // Create new file
        Configuration configuration = new Configuration("config/config.json");

        // Create object for bot
        final JSONObject bot = new JSONObject();
        bot.put("token", "defaultvalue");
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

        // Create array for games
        final JSONArray games = new JSONArray();
        games.put("defaultvalue");
        configuration.addDefault("games", games);

        // Create object for settings
        final JSONObject settings = new JSONObject();
        settings.put("prefix", "g!");
        settings.put("debug", "gt!");
        settings.put("shards", 6);
        settings.put("voice", "default");
        configuration.addDefault("settings", settings);

        // Create array for owners
        final JSONArray owners = new JSONArray();
        owners.put(264048760580079616L);
        owners.put(254892085000405004L);
        owners.put(306480135832338432L);
        configuration.addDefault("owners", owners);

        // Create object for webhooks
        final JSONObject webhook = new JSONObject();
        webhook.put("error_logger", "defaultvalue");
        webhook.put("guild_logger", "defaultvalue");
        webhook.put("user_logger", "defaultvalue");
        configuration.addDefault("webhooks", webhook);

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

        // Return config
        return configuration;
    }
}
