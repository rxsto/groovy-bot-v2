package io.groovybot.bot.io;

import io.groovybot.bot.GroovyBot;
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

    private Connection connection;

    public WebsocketConnection() throws URISyntaxException {
        super(new URI("ws://127.0.0.1:6015"));
        this.connect();
        this.connection = GroovyBot.getInstance().getPostgreSQL().getConnection();
    }

    public static JSONObject parseStats(int playing, int guilds, int users) {
        JSONObject object = new JSONObject();
        object.put("playing", playing);
        object.put("guilds", guilds);
        object.put("users", users);

        return object;
    }

    public static JSONObject parseMessage(String type, JSONObject data) {
        JSONObject object = new JSONObject();
        object.put("type", type);
        object.put("data", data);

        return object;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("[Websocket] WebsocketConnection opened!");
        authorize();
    }

    @Override
    public void onMessage(String message) {
        JSONObject object = new JSONObject(message);

        if (!object.has("type") || !object.has("data"))
            return;

        if (object.get("type").equals("forbidden"))
            authorize();

        if (object.get("type").equals("botgetstats"))
            this.send(parseMessage("poststats", parseStats(GroovyBot.getInstance().getLavalinkManager().countPlayers(), GroovyBot.getInstance().getShardManager().getGuilds().size(), GroovyBot.getInstance().getShardManager().getUsers().size())).toString());
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("[Websocket] WebsocketConnection closed! " + i + " " + s);
    }

    @Override
    public void onError(Exception e) {
        log.error("[Websocket] Error in WebsocketConnection!", e);
    }

    public void authorize() {
        String token = null;

        try {
            PreparedStatement getToken = connection.prepareStatement("SELECT * FROM websocket");
            ResultSet rs = getToken.executeQuery();
            while (rs.next()) {
                token = rs.getString("token");
            }
        } catch (SQLException e) {
            log.error("[Websocket] Error while authorizing!", e);
        }

        this.send(parseMessage("authorization", new JSONObject().put("token", token)).toString());
    }
}
