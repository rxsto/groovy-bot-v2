package co.groovybot.bot.core.prometheus;

import co.groovybot.bot.io.config.Configuration;
import io.prometheus.client.exporter.HTTPServer;
import org.json.JSONObject;

import java.io.IOException;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class Prometheus {

    private final JSONObject config;

    public Prometheus(Configuration configuration) {
        this.config = configuration.getJSONObject("prometheus");
    }

    public void connect() throws IOException {
        HTTPServer server = new HTTPServer(config.getInt("port"));
    }
}

