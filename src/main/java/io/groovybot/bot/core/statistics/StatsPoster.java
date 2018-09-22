package io.groovybot.bot.core.statistics;

import okhttp3.OkHttpClient;
import org.json.JSONObject;

import java.util.concurrent.ScheduledExecutorService;

public abstract class StatsPoster implements Runnable {

    protected final ScheduledExecutorService scheduler;
    protected final OkHttpClient okHttpClient;
    protected final JSONObject configuration;

    public StatsPoster(ScheduledExecutorService scheduler, OkHttpClient okHttpClient, JSONObject configuration) {
        this.scheduler = scheduler;
        this.okHttpClient = okHttpClient;
        this.configuration = configuration;
    }
}
