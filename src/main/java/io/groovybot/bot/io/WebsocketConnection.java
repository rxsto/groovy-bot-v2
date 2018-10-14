package io.groovybot.bot.io;

import io.groovybot.bot.GroovyBot;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;


@Log4j2
public class WebsocketConnection extends WebSocketClient {
    public WebsocketConnection() throws URISyntaxException {
        super(new URI("ws://127.0.0.1:6015"));
        this.connect();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("[Websocket] WebsocketConnection opened!");
        this.send(parseMessage("authorization", new JSONObject().put("token", GroovyBot.getInstance().getConfig().getJSONObject("websocket").getString("token"))).toString());
    }

    @Override
    public void onMessage(String message) {
        JSONObject object = new JSONObject(message);

        if (!object.has("type") || !object.has("data"))
            return;

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
}
