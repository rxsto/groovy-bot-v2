package io.groovybot.bot;

import io.groovybot.bot.io.FileManager;
import io.groovybot.bot.io.config.Configuration;
import io.groovybot.bot.io.database.PostgreSQL;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

@Log4j
public class GroovyBot {

    @Getter
    private static GroovyBot instance;
    @Getter
    private Configuration config;
    @Getter
    private PostgreSQL postgreSQL;

    public static void main(String[] args) {
        if (instance != null)
            throw new RuntimeException("Groovy was already initialized in this VM!");
        new GroovyBot(args);
    }

    private GroovyBot(String[] args) {
        instance = this;
        initLogger(args);
        log.info("Starting Groovy ...");
        new FileManager();
        initConfig();
        postgreSQL = new PostgreSQL();
        createDefaultDatabase();
    }

    private void createDefaultDatabase() {
        postgreSQL.addDefault(() -> "create table if not exists guilds\n" +
                "(\n" +
                "  id      bigint                not null\n" +
                "    constraint guilds_pkey\n" +
                "    primary key,\n" +
                "  prefix  varchar,\n" +
                "  volume  integer,\n" +
                "  dj_mode boolean default false not null\n" +
                ");");
        postgreSQL.addDefault(() -> "create table if not exists queues\n" +
                "(\n" +
                "  guild_id         bigint not null\n" +
                "    constraint table_name_pkey\n" +
                "    primary key,\n" +
                "  current_track    varchar,\n" +
                "  current_position bigint,\n" +
                "  queue            varchar,\n" +
                "  channel_id       bigint,\n" +
                "  text_channel_id  bigint\n" +
                ");");
        postgreSQL.addDefault(() -> "create table if not exists premium\n" +
                "(\n" +
                "  user_id       bigint               not null\n" +
                "    constraint premium_pkey\n" +
                "    primary key,\n" +
                "  patreon_token varchar,\n" +
                "  type          integer              not null,\n" +
                "  \"check\"       boolean default true not null,\n" +
                "  refresh_token varchar,\n" +
                "  patreon_id    varchar\n" +
                ");");
        postgreSQL.createDatabases();
    }

    private void initConfig() {
        Configuration configuration = new Configuration("config/config.json");
        final JSONObject botObject = new JSONObject();
        botObject.put("token", "defaultvalue");
        configuration.addDefault("bot", botObject);
        final JSONObject dbObject = new JSONObject();
        dbObject.put("host", "127.0.0.1");
        dbObject.put("port", 5432);
        dbObject.put("database", "defaultvalue");
        dbObject.put("username", "defaultvalue");
        dbObject.put("password", "defaultvalue");
        configuration.addDefault("db", dbObject);
        final JSONArray gamesArray = new JSONArray();
        gamesArray.put("gamename");
        configuration.addDefault("games", gamesArray);
        final JSONObject settingsObject = new JSONObject();
        settingsObject.put("prefix", "g!");
        configuration.addDefault("settings", settingsObject);
        final JSONArray ownersArray = new JSONArray();
        ownersArray.put(264048760580079616L);
        ownersArray.put(254892085000405004L);
        configuration.addDefault("owners", ownersArray);
        this.config = configuration.init();
    }

    private void initLogger(String[] args) {
        final ConsoleAppender consoleAppender = new ConsoleAppender();
        final PatternLayout consolePatternLayout = new PatternLayout("[%d{HH:mm:ss}] [%p] | %m%n");
        final FileAppender latestLogAppender = new FileAppender();
        final FileAppender dateLogAppender = new FileAppender();
        final PatternLayout filePatternLayout = new PatternLayout("[%d{dd.MMM.yyyy HH:mm:ss,SSS}] [%p] | %m%n");

        consoleAppender.setLayout(consolePatternLayout);
        consoleAppender.activateOptions();
        latestLogAppender.setLayout(filePatternLayout);
        dateLogAppender.setLayout(filePatternLayout);
        latestLogAppender.setFile("logs/latest.log");
        dateLogAppender.setFile(String.format("logs/%s.log", new SimpleDateFormat("dd_MM_yyyy-HH_mm").format(new Date())));
        latestLogAppender.activateOptions();
        dateLogAppender.activateOptions();

        Logger.getRootLogger().addAppender(consoleAppender);
        Logger.getRootLogger().addAppender(latestLogAppender);
        Logger.getRootLogger().addAppender(dateLogAppender);
        Logger.getRootLogger().setLevel(args.length == 0 ? Level.INFO : Level.toLevel(args[0]));
    }
}
