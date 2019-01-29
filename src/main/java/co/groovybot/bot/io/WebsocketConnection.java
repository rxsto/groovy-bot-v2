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

package co.groovybot.bot.io;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.LavalinkManager;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Log4j2
public class WebsocketConnection extends WebSocketClient {

    private HikariDataSource dataSource;
    private long reconnectingTimeout = 2000L;

    public WebsocketConnection() throws URISyntaxException {
        super(new URI(String.format("%s", GroovyBot.getInstance().getConfig().getJSONObject("websocket").getString("host"))));
        log.info("[Websocket] Connecting to Websocket ...");
        this.connect();
        this.dataSource = GroovyBot.getInstance().getPostgreSQL().getDataSource();
    }

    public static JSONObject parseStats(int playing, int guilds, int users) {
        JSONObject object = new JSONObject();
        object.put("playing", playing);
        object.put("guilds", guilds);
        object.put("users", users);
        return object;
    }

    public static JSONObject parseMessage(String client, String type, JSONObject data) {
        JSONObject object = new JSONObject();
        object.put("client", client);
        object.put("type", type);
        object.put("data", data);
        return object;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("[Websocket] Successfully connected to Websocket!");
        authorize();
        reconnectingTimeout = 2000L;
        this.send(WebsocketConnection.parseMessage("bot", "poststats", WebsocketConnection.parseStats(LavalinkManager.countPlayers(), GroovyBot.getInstance().getShardManager().getGuilds().size(), GroovyBot.getInstance().getShardManager().getUsers().size())).toString());
    }

    @Override
    public void onMessage(String message) {
        JSONObject object = new JSONObject(message);

        if (!object.has("type") || !object.has("data"))
            return;

        if (object.get("type").equals("error"))
            log.error("[Websocket] An error occurred! " + object.getJSONObject("data").getString("text"));

        if (object.get("type").equals("forbidden"))
            authorize();

        if (object.get("type").equals("botgetstats"))
            this.send(WebsocketConnection.parseMessage("bot", "poststats", WebsocketConnection.parseStats(LavalinkManager.countPlayers(), GroovyBot.getInstance().getShardManager().getGuilds().size(), GroovyBot.getInstance().getShardManager().getUsers().size())).toString());
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info(String.format("[Websocket] Connection closed! Trying to reconnect in %s seconds ...", reconnectingTimeout));
        try {
            Thread.sleep(reconnectingTimeout);
            new Thread(this::reconnect, "WebSocketThread").start();
            reconnectingTimeout = reconnectingTimeout + 2000L;
        } catch (InterruptedException e) {
            log.error("[Websocket] Error while reconnecting!");
        }
    }

    @Override
    public void onError(Exception e) {
        log.error("[Websocket] Error in Connection!", e);
    }

    private void authorize() {
        String token = null;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement getToken = connection.prepareStatement("SELECT * FROM websocket");
            ResultSet rs = getToken.executeQuery();
            while (rs.next())
                token = rs.getString("token");
        } catch (SQLException e) {
            log.error("[Websocket] Error while authorizing!", e);
        }

        this.send(parseMessage("bot", "authorization", new JSONObject().put("token", token)).toString());
    }
}
