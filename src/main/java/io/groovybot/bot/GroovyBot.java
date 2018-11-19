package io.groovybot.bot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.groovybot.bot.core.GameAnimator;
import io.groovybot.bot.core.KeyManager;
import io.groovybot.bot.core.PremiumManager;
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
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import net.dv8tion.jda.core.hooks.IEventManager;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import net.dv8tion.jda.core.requests.RestAction;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

@Log4j2
public class GroovyBot {

    @Getter
    private static GroovyBot instance;
    @Getter
    private final long startupTime;
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
    private final SpotifyManager spotifyClient;
    @Getter
    private final boolean enableWebsocket;
    @Getter
    private final PremiumManager premiumManager;
    @Getter
    private Configuration config;
    @Getter
    private PostgreSQL postgreSQL;
    @Getter
    private WebsocketConnection webSocket;
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
    @Setter
    private boolean allShardsInitialized = false;

    private GroovyBot(String[] args) throws IOException {

        // Setting startuptime
        startupTime = System.currentTimeMillis();

        // For debugging
        RestAction.setPassContext(true);
        RestAction.DEFAULT_FAILURE = Throwable::printStackTrace;

        instance = this;

        // Initializing logger
        initLogger(args);

        // Checking for debug-mode
        debugMode = String.join(" ", args).contains("debug");

        // Checking for webSocket-mode
        enableWebsocket = !String.join(" ", args).contains("--no-websocket");

        // Adding shutdownhook
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));

        log.info("[Core] Starting Groovy ...");

        // Initializing filemanager
        new FileManager();

        // Initializing config
        initConfig();

        // Creating cache
        guildCache = new Cache<>(Guild.class);
        userCache = new Cache<>(User.class);

        // Initializing database
        log.info("[Database] Initializing Database ...");
        postgreSQL = new PostgreSQL();

        httpClient = new OkHttpClient();
        spotifyClient = new SpotifyManager(config.getJSONObject("spotify").getString("client_id"), config.getJSONObject("spotify").getString("client_secret"));
        lavalinkManager = new LavalinkManager(this);
        statusPage = new StatusPage(httpClient, config.getJSONObject("statuspage"));

        // Generating tables
        new DatabaseGenerator(postgreSQL);

        commandManager = new CommandManager(config.getJSONObject("settings").getString("prefix"), this);
        serverCountStatistics = new ServerCountStatistics(config.getJSONObject("botlists"));
        keyManager = new KeyManager(postgreSQL.getDataSource());
        interactionManager = new InteractionManager();
        eventWaiter = new EventWaiter();

        // Initializing shardmanager
        initShardManager();

        musicPlayerManager = new MusicPlayerManager();
        translationManager = new TranslationManager();
        playlistManager = new PlaylistManager(postgreSQL.getDataSource());
        youtubeClient = YoutubeUtil.create(this);
        geniusClient = new GeniusClient(config.getJSONObject("genius").getString("token"));
        premiumManager = new PremiumManager();
        new CommandRegistry(commandManager);
    }

    public static void main(String[] args) throws IOException {
        if (instance != null)
            throw new RuntimeException("[Core] Groovy was already initialized in this VM!");
        new GroovyBot(args);
    }

    private void initShardManager() {
        eventManager = new AnnotatedEventManager();

        // Building shardmanager
        DefaultShardManagerBuilder shardManagerBuilder = new DefaultShardManagerBuilder()
                .setHttpClient(httpClient)
                .setEventManagerProvider((id) -> eventManager)
                .setToken(config.getJSONObject("bot").getString("token"))
                .setShardsTotal(-1)
                .setGame(Game.playing("Starting ..."))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(
                        this,
                        new ShardsListener(),
                        new SelfMentionListener(),
                        new JoinGuildListener(),
                        new CommandLogger(),
                        new Logger(),
                        new BlacklistWatcher(guildCache),
                        commandManager,
                        lavalinkManager,
                        interactionManager,
                        eventWaiter
                );

        if (enableWebsocket)
            shardManagerBuilder.addEventListeners(new WebsiteStatsListener());

        try {
            shardManager = shardManagerBuilder.build();
            log.info("[LavalinkManager] Initializing LavalinkManager ...");
            lavalinkManager.initialize();
        } catch (LoginException e) {
            log.error("[Core] Could not initialize bot!", e);
            Runtime.getRuntime().exit(1);
        }
    }

    private void initConfig() {
        // Initializing config
        this.config = ConfigurationSetup.setupConfig().init();
    }

    private void initLogger(String[] args) throws IOException {
        // Setting logging-level
        Configurator.setRootLevel(args.length == 0 ? Level.INFO : Level.toLevel(args[0], Level.INFO));

        // Initializing logger
        Configurator.initialize(ClassLoader.getSystemClassLoader(), new ConfigurationSource(ClassLoader.getSystemResourceAsStream("log4j2.xml")));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onReady(AllShardsLoadedEvent event) {
        // Initializing gameanimator
        new GameAnimator(this);

        // Initializing players
        try {
            log.info("[MusicPlayerManager] Initializing MusicPlayers ...");
            musicPlayerManager.initPlayers();
        } catch (SQLException | IOException e) {
            log.error("[MusicPlayerManager] Error while initializing MusicPlayers!", e);
        }

        // Initializing webSocket
        if (enableWebsocket)
            try {
                log.info("[WebSocket] Initializing WebSocket ...");
                webSocket = new WebsocketConnection();
            } catch (URISyntaxException e) {
                log.error("[WebSocket] Error while initializing WebSocket!", e);
            }

        // Initializing statuspage and servercountstatistics
        if (!debugMode) {
            statusPage.start();
            serverCountStatistics.start();
        }

        // Now Groovy is ready
        allShardsInitialized = true;
    }

    public void close() {
        try {
            if (commandManager != null)
                commandManager.close();
            if (postgreSQL != null)
                postgreSQL.close();
            if (shardManager != null)
                shardManager.shutdown();
        } catch (Exception e) {
            log.error("[Core] Error while closing bot!", e);
        }
    }
}
