/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
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

package co.groovybot.bot.core.statistics;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.util.NameThreadFactory;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j2
public class StatusPage extends StatsPoster {


    public StatusPage(OkHttpClient okHttpClient, JSONObject configuration) {
        super(Executors.newScheduledThreadPool(1, new NameThreadFactory("StatusPage")), okHttpClient, configuration);
    }

    public synchronized void start() {
        log.info("[StatusPage] Connecting to StatusPage ...");
        scheduler.scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);
        log.info("[StatusPage] Successfully connected to StatusPage!");
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
