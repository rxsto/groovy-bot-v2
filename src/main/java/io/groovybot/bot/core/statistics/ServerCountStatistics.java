package io.groovybot.bot.core.statistics;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.util.NameThreadFactory;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ServerCountStatistics extends StatsPoster {

    private GroovyBot groovyBot = GroovyBot.getInstance();
    private String botId;

    public ServerCountStatistics( JSONObject configuration) {
        super(Executors.newScheduledThreadPool(1, new NameThreadFactory("ServerCount")), new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .build()
                , configuration);
    }

    public synchronized void start() {
        //this.botId = groovyBot.getShardManager().getApplicationInfo().complete().getId();
        this.botId = "402116404301660181";
        scheduler.scheduleAtFixedRate(this, 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        JSONObject object = new JSONObject();
        object.put("server_count", String.valueOf(groovyBot.getShardManager().getGuilds().size()))
                .put("bot_id", botId)
                .put("shards_count", String.valueOf(groovyBot.getShardManager().getGuilds().size()))
                .put("botlist.space", configuration.getString("botlist.space"))
                .put("bots.ondiscord.xyz", configuration.getString("bots.ondiscord.xyz"))
                .put("discordboats.xyz", configuration.getString("discordboats.xyz"))
                .put("discordboats.club", configuration.getString("discordboats.club"))
                .put("discordbotlist.com", configuration.getString("discordbotlist.com"))
                .put("discordbot.world", configuration.getString("discordbot.world"))
                .put("bots.discord.pw", configuration.getString("bots.discord.pw"))
                .put("discordbotlist.xyz", configuration.getString("discordbotlist.xyz"))
                .put("discordbots.group", configuration.getString("discordbots.group"))
                .put("bots.discordlist.app", configuration.getString("bots.discordlist.app"))
                .put("discord.services", configuration.getString("discord.services"))
                .put("discordsbestbots.xyz", configuration.getString("discordsbestbots.xyz"))
                .put("divinediscordbots.com", configuration.getString("divinediscordbots.com"))
                .put("discordbots.org", configuration.getString("discordbots.org"))
                .put("discordbotindex.com", configuration.getString("discordbotindex.com"))
                .put("shards", getGuildCounts());
        RequestBody body = RequestBody.create(null, object.toString());
        Request request = new Request.Builder()
                .url("https://botblock.org/api/count")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", botId)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                assert response.body() != null;
                log.warn(String.format("[ServerCount] Error while posting stats! Response: %s", response.body().string()));
            }
        } catch (IOException e) {
            log.error("[ServerCount] Error while posting stats!", e);
        }
    }

    private Integer[] getGuildCounts() {
        List<Integer> shardGuildCounts = new ArrayList<>();
        groovyBot.getShardManager().getShards().forEach(shard -> shardGuildCounts.add(shard.getGuilds().size()));
        return shardGuildCounts.toArray(new Integer[0]);
    }
}
