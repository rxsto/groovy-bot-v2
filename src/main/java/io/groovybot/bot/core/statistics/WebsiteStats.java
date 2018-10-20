package io.groovybot.bot.core.statistics;

import com.zaxxer.hikari.HikariDataSource;
import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.util.NameThreadFactory;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
@Deprecated
public class WebsiteStats implements Runnable {

    private final HikariDataSource dataSource;
    private final GroovyBot groovyBot;

    public WebsiteStats(GroovyBot groovyBot) {
        this.groovyBot = groovyBot;
        this.dataSource = groovyBot.getPostgreSQL().getDataSource();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new NameThreadFactory("WebsiteStats"));
        scheduler.scheduleAtFixedRate(this, 0, 20, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("DELETE FROM stats").execute();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO stats (playing, servers, users, id) VALUES (?, ?, ?, ?)");
            ps.setInt(1, groovyBot.getMusicPlayerManager().getPlayingServers());
            ps.setInt(2, groovyBot.getShardManager().getGuilds().size());
            ps.setInt(3, groovyBot.getShardManager().getUsers().size());
            ps.setLong(4, 402116404301660181L);
            ps.execute();
            log.debug("[Stats] Posted stats");
        } catch (SQLException e) {
            log.error("[Stats] Could not save website stats", e);
        }
    }
}
