package co.groovybot.bot.core;

import co.groovybot.bot.GroovyBot;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class CustomMetricsServlet extends HttpServlet {

    private final CollectorRegistry registry = CollectorRegistry.defaultRegistry;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        String code = req.getParameter("token");

        try (Writer writer = resp.getWriter()) {
            if (code == null || !code.equals(GroovyBot.getInstance().getConfig().getJSONObject("prometheus").getString("token"))) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.setContentType("text/plain");
                writer.write("unauthorized");
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(TextFormat.CONTENT_TYPE_004);
            TextFormat.write004(writer, registry.filteredMetricFamilySamples(parse(req)));
            writer.flush();
        }
    }

    private Set<String> parse(HttpServletRequest req) {
        String[] includedParam = req.getParameterValues("name[]");
        if (includedParam == null) {
            return Collections.emptySet();
        } else {
            return new HashSet<String>(Arrays.asList(includedParam));
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }
}
