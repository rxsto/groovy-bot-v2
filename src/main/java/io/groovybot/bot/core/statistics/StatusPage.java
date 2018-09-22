package io.groovybot.bot.core.statistics;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.util.NameThreadFactory;
import lombok.extern.log4j.Log4j;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j
public class StatusPage extends StatsPoster {


    public StatusPage(OkHttpClient okHttpClient, JSONObject configuration) {
        super(Executors.newScheduledThreadPool(1, new NameThreadFactory("StatusPage")), okHttpClient, configuration);
    }

    public synchronized void start() {
        scheduler.scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data[value]", String.valueOf(GroovyBot.getInstance().getShardManager().getAveragePing()))
                .addFormDataPart("data[timestamp]", getTimeStamp())
                .build();
        Request request = new Request.Builder()
                .url(String.format("https://api.statuspage.io/v1/pages/%s/metrics/%s/data.json", configuration.getString("page_id"), configuration.getString("metric_id")))
                .addHeader("Authorization", configuration.getString("api_key"))
                .post(body)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            assert response.body() != null;
            if (response.code() != 201) {
                log.warn(String.format("[StatusPage] Got an unexpected response %s", response.body().string()));
            }
            log.debug(String.format("[StatusPage] Posted ping to StatusPage! Submitted ping: %s", new JSONObject(response.body().string()).getJSONObject("data").getFloat("value")));
        } catch (IOException e) {
            log.error("[StatusPage] Error while posting ping!", e);
        }
    }

    private String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000L);
    }
}
