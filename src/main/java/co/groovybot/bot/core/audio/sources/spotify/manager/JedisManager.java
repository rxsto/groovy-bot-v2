package co.groovybot.bot.core.audio.sources.spotify.manager;

import redis.clients.jedis.Jedis;

public class JedisManager {

    private final Jedis jedis;

    public JedisManager(String host, int port) {
        this.jedis = new Jedis(host, port);
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
