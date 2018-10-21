package io.groovybot.bot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.groovybot.bot.core.GameAnimator;
import io.groovybot.bot.core.KeyManager;
import io.groovybot.bot.core.audio.LavalinkManager;
import io.groovybot.bot.core.audio.MusicPlayerManager;
import io.groovybot.bot.core.audio.PlaylistManager;
import io.groovybot.bot.core.audio.spotify.SpotifyManager;
import io.groovybot.bot.core.cache.Cache;
import io.groovybot.bot.core.command.CommandManager;
import io.groovybot.bot.core.command.CommandRegistry;
import io.groovybot.bot.core.command.interaction.InteractionManager;
import io.groovybot.bot.core.entity.Guild;
import io.groovybot.bot.core.entity.User;
import io.groovybot.bot.core.events.bot.AllShardsLoadedEvent;
import io.groovybot.bot.core.lyrics.GeniusClient;
import io.groovybot.bot.core.statistics.ServerCountStatistics;
import io.groovybot.bot.core.statistics.StatusPage;
import io.groovybot.bot.core.translation.TranslationManager;
import io.groovybot.bot.io.FileManager;
import io.groovybot.bot.io.WebsocketConnection;
import io.groovybot.bot.io.config.Configuration;
import io.groovybot.bot.io.config.ConfigurationSetup;
import io.groovybot.bot.io.database.DatabaseGenerator;
import io.groovybot.bot.io.database.PostgreSQL;
import io.groovybot.bot.listeners.*;
import io.groovybot.bot.util.YoutubeUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import net.dv8tion.jda.core.hooks.IEventManager;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

@Log4j2
public class GroovyBot {

    @Getter
    private static GroovyBot instance;
    @Getter
    private final OkHttpClient httpClient;
    @Getter
    private final CommandManager commandManager;
    @Getter
    private final boolean debugMode;
    @Getter
    private final TranslationManager translationManager;
    @Getter
    private final LavalinkManager lavalinkManager;
    private final StatusPage statusPage;
    private final ServerCountStatistics serverCountStatistics;
    @Getter
    private final MusicPlayerManager musicPlayerManager;
    @Getter
    private final InteractionManager interactionManager;
    @Getter
    private final EventWaiter eventWaiter;
    @Getter
    private final KeyManager keyManager;
    @Getter
    private final YoutubeUtil youtubeClient;
    @Getter
    private final GeniusClient geniusClient;
    @Getter
    private final SpotifyManager spotifyManager;
    @Getter
    private Configuration config;
    @Getter
    private PostgreSQL postgreSQL;
    @Getter
    private WebsocketConnection websocket;
    @Getter
    private ShardManager shardManager;
    @Getter
    private IEventManager eventManager;
    @Getter
    private Cache<Guild> guildCache;
    @Getter
    private Cache<User> userCache;
    @Getter
    private PlaylistManager playlistManager;
    @Getter
    private boolean allShardsInitialized = false;
    @Getter
    private final boolean enableWebsocket;

    private GroovyBot(String[] args) {
        instance = this;
        initLogger(args);
        debugMode = String.join(" ", args).contains("debug");
        enableWebsocket = !String.join(" ", args).contains("--no-websocket");
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        log.info("Starting Groovy ...");
        new FileManager();
        initConfig();
        httpClient = new OkHttpClient();
        postgreSQL = new PostgreSQL();
        lavalinkManager = new LavalinkManager(this);
        statusPage = new StatusPage(httpClient, config.getJSONObject("statuspage"));
        new DatabaseGenerator(postgreSQL);
        commandManager = new CommandManager(debugMode ? config.getJSONObject("settings").getString("test_prefix") : config.getJSONObject("settings").getString("prefix"), this);
        serverCountStatistics = new ServerCountStatistics(config.getJSONObject("botlists"));
        keyManager = new KeyManager(postgreSQL.getDataSource());
        interactionManager = new InteractionManager();
        eventWaiter = new EventWaiter();
        initShardManager();
        musicPlayerManager = new MusicPlayerManager();
        translationManager = new TranslationManager();
        playlistManager = new PlaylistManager(postgreSQL.getDataSource());
        youtubeClient = YoutubeUtil.create(this);
        geniusClient = new GeniusClient(config.getJSONObject("genius").getString("token"));
        new CommandRegistry(commandManager);
        spotifyManager = new SpotifyManager(config.getJSONObject("spotify").getString("client_id"), config.getJSONObject("spotify").getString("client_token"));
    }

    public static void main(String[] args) {
        if (instance != null)
            throw new RuntimeException("Groovy was already initialized in this VM!");
        new GroovyBot(args);
    }

    private Integer retrieveShards() {
        Request request = new Request.Builder()
                .url("https://discordapp.com/api/gateway/bot")
                .addHeader("Authorization", config.getJSONObject("bot").getString("token"))
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            assert response.body() != null;
            return new JSONObject(response.body().string()).getInt("shards");
        } catch (IOException e) {
            log.warn("[JDA] Error while retrieving shards count");
            return config.getJSONObject("settings").getInt("maxShards");
        }
    }

    private void initShardManager() {
        eventManager = new AnnotatedEventManager();
        DefaultShardManagerBuilder shardManagerBuilder = new DefaultShardManagerBuilder()
                .setHttpClient(httpClient)
                .setEventManagerProvider((id) -> eventManager)
                .setToken(config.getJSONObject("bot").getString("token"))
                .setShardsTotal(retrieveShards())
                .setGame(Game.playing("Starting ..."))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(
                        new ShardsListener(),
                        new CommandLogger(),
                        new GuildLogger(),
                        new SelfMentionListener(),
                        this,
                        commandManager,
                        lavalinkManager,
                        interactionManager,
                        eventWaiter
                );
        if (enableWebsocket)
            shardManagerBuilder.addEventListeners(new WebsiteStatsListener());
        try {
            shardManager = shardManagerBuilder.build();
            lavalinkManager.initialize();
        } catch (LoginException e) {
            log.error("[JDA] Could not initialize bot!", e);
            Runtime.getRuntime().exit(1);
        }
    }

    private void initConfig() {
        this.config = ConfigurationSetup.setupConfig().init();
    }

    private void initLogger(String[] args) {
        Configurator.setRootLevel(args.length == 0 ? Level.INFO : Level.toLevel(args[0], Level.INFO));
        try {
            Configurator.initialize(ClassLoader.getSystemClassLoader(), new ConfigurationSource(ClassLoader.getSystemResourceAsStream("log4j2.xml")));
        } catch (IOException e) {
            System.err.println("Error while initializing logger");
            close();
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onReady(AllShardsLoadedEvent event) {
        allShardsInitialized = true;
        new GameAnimator(this);
        guildCache = new Cache<>(Guild.class);
        userCache = new Cache<>(User.class);

        try {
            musicPlayerManager.initPlayers();
        } catch (SQLException | IOException e) {
            log.error("[MusicPlayerManager] Error while initializing MusicPlayers!", e);
        }


        if (!debugMode) {
            statusPage.start();
            serverCountStatistics.start();
        }
    }

    public void close() {
        try {
            if (postgreSQL != null)
                postgreSQL.close();
            if (shardManager != null)
                shardManager.shutdown();
        } catch (Exception e) {
            log.error("[Core] Error while closing bot!", e);
        }
    }
}
