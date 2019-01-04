package co.groovybot.bot.core.audio.sources.spotify.manager;

import org.json.JSONObject;
import redis.clients.jedis.Jedis;

public class JedisManager {

    private final Jedis jedis;

    public JedisManager(JSONObject jsonObject) {
        this.jedis = new Jedis(jsonObject.getString("host"), jsonObject.getInt("port"));
        this.jedis.auth(jsonObject.getString("password"));
    }

    public void set(String key, String value) {
        this.jedis.set(key, value);
    }

    public String get(String key) {
        return this.jedis.get(key);
    }

    public Jedis getJedis() {
        return this.jedis;
    }
}
