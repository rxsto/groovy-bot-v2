package io.groovybot.bot.io;

import io.groovybot.bot.GroovyBot;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

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
    }

    @Override
    public void onMessage(String message) {
        if (message.equals("botgetstats")) {
            this.send(String.format("%s-poststats:%s:%s:%s", GroovyBot.getInstance().getConfig().getJSONObject("websocket").getString("token"), GroovyBot.getInstance().getLavalinkManager().countPlayers(), GroovyBot.getInstance().getShardManager().getGuilds().size(), GroovyBot.getInstance().getShardManager().getUsers().size()));
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("[Websocket] WebsocketConnection closed! " + i + " " + s);
    }

    @Override
    public void onError(Exception e) {
        log.error("[Websocket] Error in WebsocketConnection!", e);
    }
}
