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

package co.groovybot.bot.io.config;

import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class Configuration extends JSONObject {

    private final File configFile;
    private final Map<String, Object> defaults;

    public Configuration(String fileName) {
        this(new File(fileName));
    }

    private Configuration(File file) {
        this.configFile = file;
        this.defaults = new HashMap<>();
    }

    public Configuration init() {
        String content = null;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile))) {
            if (configFile.exists())
                content = bufferedReader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.error("[Configuration] Error while loading config!", e);
        }
        if (content == null || content.equals("")) {
            log.warn("[Configuration] The config does not exists, started loading defaults!");
            defaults.forEach(this::put);
        } else {
            JSONObject object = checkObject(new JSONObject(content));
            object.toMap().forEach((key, value) -> put(key, (value instanceof Map ? (new JSONObject(((Map) value))) : (new JSONArray(listToString(((List<?>) value)))))));
        }
        save();
        return this;
    }

    public void addDefault(String key, Object value) {
        if (!(value instanceof JSONObject) && !(value instanceof JSONArray))
            throw new IllegalArgumentException("Object value needs to be a JSONObject or JSONArray!");
        defaults.put(key, value);
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile))) {
            bufferedWriter.write(toString(2));
        } catch (IOException e) {
            log.error("[Configuration] Error while saving Configuration!", e);
        }
    }

    private String listToString(List<?> list) {
        StringBuilder builder = new StringBuilder()
                .append("[");
        list.forEach(element -> builder.append("\"").append(element.toString()).append("\"").append(","));
        builder.append("]");
        if (builder.toString().contains(","))
            builder.replace(builder.lastIndexOf(","), builder.lastIndexOf(",") + 1, "");
        return builder.toString();
    }

    private JSONObject checkObject(JSONObject jsonObject) {
        defaults.forEach((key, value) -> {
            if (!jsonObject.has(key))
                jsonObject.put(key, value);
            else {
                Object defaultObject = defaults.get(key);
                //Ignore JSONArrays
                if (!(defaultObject instanceof JSONObject))
                    return;
                JSONObject existingObject = jsonObject.getJSONObject(key);
                ((JSONObject) defaultObject).toMap().forEach((defaultKey, defaultValue) -> {
                    if (!existingObject.has(defaultKey))
                        existingObject.put(defaultKey, defaultValue);
                });
            }
        });
        return jsonObject;
    }
}
