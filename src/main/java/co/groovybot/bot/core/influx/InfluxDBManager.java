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

package co.groovybot.bot.core.influx;

import co.groovybot.bot.io.config.Configuration;
import lombok.extern.log4j.Log4j2;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.json.JSONObject;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
@Log4j2
public class InfluxDBManager {

    private final JSONObject config;

    public InfluxDBManager(Configuration config) {
        this.config = config.getJSONObject("influxdb");
    }

    public InfluxDB build() {
        log.info("[InfluxDBManager] Connection to InfluxDB...");
        InfluxDB influxDB = InfluxDBFactory.connect(String.format("http://%s:8086", config.getString("host")),
                config.getString("username"), config.getString("password"));
        Pong res = influxDB.ping();
        log.info("[InfluxDBManager] Version: " + res.getVersion());
        if (res.getVersion().equalsIgnoreCase("unknown")) {
            log.fatal("[InfluxDBManager] Failed to connect to InfluxDB!");
            return null;
        }
        log.info("[InfluxDBManager] Successfully connected to InfluxDB {}", res.getVersion());

        String dbName = config.getString("database");
        String retentionPolicy = "groovyRetentionPolicy";
        influxDB.createRetentionPolicy(retentionPolicy, dbName, "7d", "1d", 2, true);
        if (!influxDB.databaseExists(dbName)) {
            influxDB.createDatabase(dbName);
        }
        influxDB.setDatabase(dbName);
        influxDB.setRetentionPolicy(retentionPolicy);
        return influxDB;
    }
}
