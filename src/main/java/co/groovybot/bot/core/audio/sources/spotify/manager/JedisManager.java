package co.groovybot.bot.core.audio.sources.spotify.manager;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

@Log4j2
public class JedisManager {

    private final Jedis jedis;

    public JedisManager(JSONObject jsonObject) {
        this.jedis = new Jedis(jsonObject.getString("host"));
        this.jedis.auth(jsonObject.getString("password"));
        if (this.jedis.isConnected())
            log.debug(this.jedis.ping("PINGED"));
    }

    public void set(String key, String value) {
        this.jedis.set(key, value);
    }

    public String get(String key) {
        return this.jedis.get(key);
    }

    public void close() {
        if (this.jedis != null)
            if (this.jedis.isConnected())
                this.jedis.close();
    }

    public Jedis getJedis() {
        return this.jedis;
    }
}
