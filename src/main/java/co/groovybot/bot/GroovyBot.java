/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergeij Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
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

package co.groovybot.bot;

import co.groovybot.bot.core.GameAnimator;
import co.groovybot.bot.core.audio.LavalinkManager;
import co.groovybot.bot.core.audio.MusicPlayerManager;
import co.groovybot.bot.core.audio.playlists.PlaylistManager;
import co.groovybot.bot.core.audio.spotify.SpotifyManager;
import co.groovybot.bot.core.cache.Cache;
import co.groovybot.bot.core.command.CommandManager;
import co.groovybot.bot.core.command.CommandRegistry;
import co.groovybot.bot.core.command.interaction.InteractionManager;
import co.groovybot.bot.core.entity.Guild;
import co.groovybot.bot.core.entity.User;
import co.groovybot.bot.core.events.bot.AllShardsLoadedEvent;
import co.groovybot.bot.core.influx.InfluxDBManager;
import co.groovybot.bot.core.lyrics.GeniusClient;
import co.groovybot.bot.core.monitoring.Monitor;
import co.groovybot.bot.core.monitoring.MonitorManager;
import co.groovybot.bot.core.monitoring.monitors.*;
import co.groovybot.bot.core.premium.PremiumHandler;
import co.groovybot.bot.core.statistics.ServerCountStatistics;
import co.groovybot.bot.core.statistics.StatusPage;
import co.groovybot.bot.core.translation.TranslationManager;
import co.groovybot.bot.io.FileManager;
import co.groovybot.bot.io.WebsocketConnection;
import co.groovybot.bot.io.config.Configuration;
import co.groovybot.bot.io.config.ConfigurationSetup;
import co.groovybot.bot.io.database.DatabaseGenerator;
import co.groovybot.bot.io.database.PostgreSQL;
import co.groovybot.bot.listeners.*;
import co.groovybot.bot.util.YoutubeUtil;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
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
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.graylog2.gelfclient.GelfConfiguration;
import org.graylog2.gelfclient.GelfMessage;
import org.graylog2.gelfclient.GelfTransports;
import org.graylog2.gelfclient.transport.AbstractGelfTransport;
import org.graylog2.gelfclient.transport.GelfTransport;
import org.influxdb.InfluxDB;

import javax.security.auth.login.LoginException;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Objects;

@Log4j2
public class GroovyBot implements Closeable {

    @Getter
    private static GroovyBot instance;
    @Getter
    private final String instanceName;
    @Getter
    private final OkHttpClient httpClient;
    @Getter
    private final CommandManager commandManager;
    @Getter
    private final TranslationManager translationManager;
    @Getter
    private final LavalinkManager lavalinkManager;
    @Getter
    private final StatusPage statusPage;
    @Getter
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
    private final PremiumHandler premiumHandler;
    @Getter
    private final Configuration config;
    @Getter
    private final long startupTime;
    @Getter
    private final boolean debugMode;
    @Getter
    private final boolean configNodes;
    @Getter
    private final boolean noWebsocket;
    @Getter
    private final boolean premium;
    @Getter
    private final boolean noJoin;
    @Getter
    private final boolean noPatrons;
    @Getter
    private final boolean noMonitoring;
    @Getter
    private final boolean noCentralizedLogging;
    @Getter
    private PostgreSQL postgreSQL;
    @Getter
    private MonitorManager monitorManager;
    @Getter
    private InfluxDB influxDB;
    @Getter
    private WebsocketConnection webSocket;
    @Getter
    private ShardManager shardManager;
    @Getter
    private IEventManager eventManager;
    @Getter
    private PlaylistManager playlistManager;
    @Getter
    private GelfTransport gelfTransport;
    @Getter
    private Cache<Guild> guildCache;
    @Getter
    private Cache<User> userCache;
    @Getter
    private net.dv8tion.jda.core.entities.Guild supportGuild;
    @Getter
    private boolean allShardsInitialized = false;

    private GroovyBot(CommandLine args) throws IOException {

        // Setting startuptime
        startupTime = System.currentTimeMillis();

        instance = this;

        // Initializing logger
        initLogger(args);

        // Checking for args
        debugMode = args.hasOption("debug");
        premium = args.hasOption("premium");
        noWebsocket = args.hasOption("no-websocket");
        noJoin = args.hasOption("no-voice-join");
        noPatrons = args.hasOption("no-patrons");
        noMonitoring = args.hasOption("no-monitoring");
        configNodes = args.hasOption("config-nodes");
        noCentralizedLogging = args.hasOption("no-centralized-logging");

        // Adding shutdownhook
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));

        log.info("[Core] Starting Groovy ...");

        // Initializing filemanager
        new FileManager();

        // Initializing config
        config = ConfigurationSetup.setupConfig().init();
        instanceName = config.getJSONObject("bot").has("instance") ? config.getJSONObject("bot").getString("instance") : "dev";

        // Creating cache
        guildCache = new Cache<>(Guild.class);
        userCache = new Cache<>(User.class);

        if (!noCentralizedLogging) {
            final GelfConfiguration gelfConfiguration = new GelfConfiguration(new InetSocketAddress(config.getJSONObject("graylog").getString("host"), config.getJSONObject("graylog").getInt("port")));
            gelfConfiguration.transport(GelfTransports.TCP).queueSize(512).connectTimeout(5000).reconnectDelay(1000).tcpNoDelay(true).sendBufferSize(32768);
            gelfTransport = GelfTransports.create(gelfConfiguration);
            try {
                gelfTransport.send(new GelfMessage("HELLO"));
                log.info("[Graylog] Successfully connected to Graylog!");
            } catch (InterruptedException e) {
                gelfTransport = null;
                log.warn("[Graylog] Connection failed!");
            }
        }

        // Initializing database
        log.info("[Database] Initializing Database ...");
        postgreSQL = new PostgreSQL();

        // Check for --no-monitoring and initialize InfluxDB if not
        if (!noMonitoring) influxDB = new InfluxDBManager(config).build();

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
        premiumHandler = new PremiumHandler();

        // Initializing shardmanager
        initShardManager();

        musicPlayerManager = new MusicPlayerManager();
        translationManager = new TranslationManager();
        playlistManager = new PlaylistManager(postgreSQL.getDataSource());
        youtubeClient = YoutubeUtil.create(this);
        geniusClient = new GeniusClient(config.getJSONObject("genius").getString("token"));
        new CommandRegistry(commandManager);
    }

    public static void main(String[] args) throws IOException {
        if (instance != null)
            throw new RuntimeException("[Core] Groovy was already initialized in this VM!");
        Options options = new Options();
        options.addOption("L", "log-level", true, "Let's you set the loglevel of groovy");
        options.addOption("D", "debug", false, "Let's you enable debug mode");
        options.addOption("P", "premium", false, "Let's you enable premium mode");
        options.addOption("CN", "config-nodes", false, "Let's you load nodes from config");
        options.addOption("NW", "no-websocket", false, "Disables connection to stats socket");
        options.addOption("NV", "no-voice-join", false, "Disable automatic voice channel joining on Groovy support server");
        options.addOption("NM", "no-monitoring", false, "Disables InfluxDB monitoring");
        options.addOption("NP", "no-patrons", false, "Disable patrons feature");
        options.addOption("NCL", "no-centralized-logging", false, "Disabled centralized logging");
        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(e.getMessage(), options);
        }
        new GroovyBot(cmd);
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
                        new BlacklistWatcher(guildCache),
                        new AutopauseListener(),
                        new GuildLeaveListener(),
                        new AutoJoinExecutor(this),
                        new AutoQueueListener(this),
                        commandManager,
                        lavalinkManager,
                        interactionManager,
                        eventWaiter
                );

        if (!noWebsocket)
            shardManagerBuilder.addEventListeners(new WebsiteStatsListener());
        if (!noPatrons)
            shardManagerBuilder.addEventListeners(new PremiumListener(premiumHandler));
        if (premium)
            shardManagerBuilder.addEventListeners(new PremiumExecutor(this));
        try {
            shardManager = shardManagerBuilder.build();
            log.info("[LavalinkManager] Initializing LavalinkManager ...");
            lavalinkManager.initialize();
        } catch (LoginException e) {
            log.error("[Core] Could not initialize bot!", e);
            Runtime.getRuntime().exit(1);
        }
    }

    private void initLogger(CommandLine args) throws IOException {
        Configurator.setRootLevel(Level.toLevel(args.getOptionValue("log-level", "INFO")));
        Configurator.setLevel(AbstractGelfTransport.class.getName(), Level.INFO);
        Configurator.initialize(ClassLoader.getSystemClassLoader(), new ConfigurationSource(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("log4j2.xml"))));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onReady(AllShardsLoadedEvent event) {
        // Initializing gameanimator
        new GameAnimator(this);

        // Initializing players
        try {
            log.info("[MusicPlayerManager] Initializing MusicPlayers ...");
            musicPlayerManager.initPlayers(noJoin);
        } catch (SQLException | IOException e) {
            log.error("[MusicPlayerManager] Error while initializing MusicPlayers!", e);
        }

        supportGuild = shardManager.getGuildById(403882830225997825L);

        // Register all Donators
        if (!noPatrons) {
            try {
                log.info("[PremiumHandler] Initializing Patrons ...");
                premiumHandler.initializePatrons(supportGuild, postgreSQL.getDataSource().getConnection());
            } catch (SQLException | NullPointerException e) {
                log.error("[PremiumHandler] Error while initializing Patrons!", e);
            }
        }

        // Initializing webSocket
        if (!noWebsocket)
            try {
                log.info("[WebSocket] Initializing WebSocket ...");
                webSocket = new WebsocketConnection();
            } catch (URISyntaxException e) {
                log.error("[WebSocket] Error while initializing WebSocket!", e);
            }

        // Initializing statuspage and servercountstatistics
        if (!debugMode) {
            log.info("[StatusPage] Initializing StatusPage ...");
            statusPage.start();
            log.info("[ServerCountStatistics] Initializing ServerCountStatistics ...");
            serverCountStatistics.start();
        }

        // Register all monitors and start monitoring
        if (influxDB == null) {
            log.info("[MonitoringManager] Monitoring disabled, because there is no connection to InfluxDB!");
        } else {
            monitorManager = new MonitorManager(influxDB);
            Monitor msgMonitor = new MessageMonitor();
            shardManager.addEventListener(msgMonitor);
            monitorManager.register(new SystemMonitor(), new GuildMonitor(), new RequestMonitor(), msgMonitor, new UserMonitor());
            monitorManager.start();
            log.info("[MonitoringManager] Monitoring started.");
        }

        // Now Groovy is ready
        allShardsInitialized = true;
        log.info("[Core] Successfully launched Groovy!");
    }

    @Override
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
