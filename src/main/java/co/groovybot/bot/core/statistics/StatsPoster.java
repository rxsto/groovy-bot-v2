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
